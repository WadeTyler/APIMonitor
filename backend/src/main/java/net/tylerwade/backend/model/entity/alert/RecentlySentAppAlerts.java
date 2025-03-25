package net.tylerwade.backend.model.entity.alert;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "RecentlySentAppAlerts", timeToLive = 600L)
public class RecentlySentAppAlerts {

    @Id
    private String appId;

    public RecentlySentAppAlerts() {
    }

    public RecentlySentAppAlerts(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
