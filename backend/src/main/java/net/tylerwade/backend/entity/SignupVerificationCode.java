package net.tylerwade.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "SignupVerificationCode", timeToLive = 300L)
public class SignupVerificationCode {

    @Id
    private String verificationCode;
    private String email;

    public SignupVerificationCode(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public SignupVerificationCode() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
