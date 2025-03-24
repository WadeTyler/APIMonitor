package net.tylerwade.backend.model.entity.alert;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String appId;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "alert_config_id") // Foreign key stored in AlertFields
    private List<AlertField> alertFields = new ArrayList<>();


    public AlertConfig() {
    }

    public AlertConfig(String appId) {
        this.appId = appId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<AlertField> getAlertFields() {
        return alertFields;
    }

    public void setAlertFields(List<AlertField> alertFields) {
        this.alertFields = alertFields;
    }
}
