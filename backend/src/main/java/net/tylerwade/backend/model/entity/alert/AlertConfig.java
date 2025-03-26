package net.tylerwade.backend.model.entity.alert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.tylerwade.backend.model.entity.Application;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "app_id")
    @JsonIgnore
    private Application app;

    private boolean emailAlertsEnabled = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "alert_config_id") // Foreign key stored in AlertFields
    private List<AlertField> alertFields = new ArrayList<>();

    public AlertConfig() {
    }

    public AlertConfig(Application app) {
        this.app = app;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApp() {
        return this.app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public List<AlertField> getAlertFields() {
        return alertFields;
    }

    public void setAlertFields(List<AlertField> alertFields) {
        this.alertFields = alertFields;
    }

    public boolean isEmailAlertsEnabled() {
        return emailAlertsEnabled;
    }

    public void setEmailAlertsEnabled(boolean emailAlertsEnabled) {
        this.emailAlertsEnabled = emailAlertsEnabled;
    }
}
