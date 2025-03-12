package net.tylerwade.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

// Expires after 10 mins
@RedisHash(value = "PasswordChangeAttempt", timeToLive = 600L)
public class PasswordChangeAttempt {

    @Id
    private String userId;
    private int count;

    public PasswordChangeAttempt(String userId, int count) {
        this.userId = userId;
        this.count = count;
    }

    public PasswordChangeAttempt() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
