package net.tylerwade.backend.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "services")
public class Service {

    @Id
    private String id = UUID.randomUUID().toString().toUpperCase() + "-" + UUID.randomUUID().toString().toUpperCase();
    private String publicToken = UUID.randomUUID().toString().toLowerCase();
    private String userId;
    private String name;

    public Service() {
    }

    public Service(String userId, String name) {
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
