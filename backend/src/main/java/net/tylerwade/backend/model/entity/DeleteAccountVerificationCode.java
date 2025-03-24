package net.tylerwade.backend.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "DeleteAccountVerificationCode", timeToLive = 300L)
public class DeleteAccountVerificationCode {

    @Id
    private String userId;
    private String verificationCode;

    public DeleteAccountVerificationCode(String userId, String verificationCode) {
        this.userId = userId;
        this.verificationCode = verificationCode;
    }

    public DeleteAccountVerificationCode() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
