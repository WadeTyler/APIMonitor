package net.tylerwade.backend.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "BlacklistedJwt", timeToLive = 1440)
public class BlacklistedJwt {

    @Id
    private String token;

    public BlacklistedJwt(String token) {
        this.token = token;
    }

    public BlacklistedJwt() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
