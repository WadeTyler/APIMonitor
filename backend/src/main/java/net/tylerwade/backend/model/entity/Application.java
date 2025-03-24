package net.tylerwade.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    private String id = UUID.randomUUID().toString().toUpperCase() + "-" + UUID.randomUUID().toString().toUpperCase();
    private String publicToken = UUID.randomUUID().toString().toLowerCase();
    private String userId;
    private String name;

    public Application() {
    }

    public Application(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }
}
