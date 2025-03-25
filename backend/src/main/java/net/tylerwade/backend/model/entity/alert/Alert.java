package net.tylerwade.backend.model.entity.alert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.tylerwade.backend.model.entity.APICall;
import net.tylerwade.backend.model.entity.Application;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "app_id")
    @JsonIgnore
    private Application app;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "alert_field_id")
    private AlertField alertField;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "api_call_id")
    private APICall apiCall;

    public Alert() {
    }

    public Alert(Application app, AlertField alertField, APICall apiCall) {
        this.app = app;
        this.alertField = alertField;
        this.apiCall = apiCall;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlertField getAlertField() {
        return alertField;
    }

    public void setAlertField(AlertField alertField) {
        this.alertField = alertField;
    }

    public APICall getApiCall() {
        return apiCall;
    }

    public void setApiCall(APICall apiCall) {
        this.apiCall = apiCall;
    }

}
