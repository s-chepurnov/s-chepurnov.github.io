package hexlet.code;

import io.javalin.Javalin;

class App {

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create();
        app.create(config -> {
            config.enableDevLogging(); // enable extensive development logging for http and websocket
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8080");
        return Integer.valueOf(port);
    }

}
