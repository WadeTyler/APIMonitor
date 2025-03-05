package net.tylerwade.backend.APIMonitorLib;

import org.springframework.stereotype.Component;

@Component
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "monitor")
public class ConfigurationProperties {

    private String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
