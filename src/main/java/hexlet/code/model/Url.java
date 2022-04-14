package hexlet.code.model;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Random;

@Entity
public class Url extends Model {

    @Id
    long id;

    String name;
    Instant createdAt;

    public Url(String name) {
        this.name = name;
        this.id = new Random().nextLong();
        createdAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
