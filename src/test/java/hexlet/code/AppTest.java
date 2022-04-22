package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AppTest {

    private static Javalin app;
    private static MockWebServer server;
    private static String baseUrl;
    private static Url existingUrl;
    private static Transaction transaction;
    private final int successStatus = 200;
    private static final String EXAMPLE_URL = "https://www.example.com";

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();

        baseUrl = "http://localhost:" + port;
        existingUrl = new Url(EXAMPLE_URL);
        existingUrl.save();

        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        server.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @Nested
    class RootTest {

        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(successStatus);
            assertThat(response.getBody()).contains("Анализатор страниц");
        }
    }

    @Nested
    class UrlTest {

        @Test
        void testList() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(successStatus);
            assertThat(body).contains("Сайты");
        }

        @Test
        void testShow() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/" + existingUrl.getId())
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(successStatus);
            assertThat(body).contains(existingUrl.getName());
        }

        @Test
        void testCreate() {

            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("name", EXAMPLE_URL)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(successStatus);

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(successStatus);
            assertThat(body).contains(EXAMPLE_URL);

            Url dbUrl = new QUrl()
                    .name.equalTo(EXAMPLE_URL)
                    .findOne();

            assertThat(dbUrl).isNotNull();
            assertThat(dbUrl.getName()).isEqualTo(EXAMPLE_URL);
        }

        @Test
        void testCheck() throws IOException {
            String htmlFile = "src/test/resources/show_example.html";
            String html = Files.readString(Path.of(htmlFile));
            server.enqueue(new MockResponse().setBody(html));

            HttpUrl baseUrl = server.url("/urls/1/checks");
            HttpResponse<String> response = Unirest
                    .post(baseUrl.toString())
                    .asString();
            String body = response.getBody();

            assertThat(body).contains("Проверки");
        }
    }

}
