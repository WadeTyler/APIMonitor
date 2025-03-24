package net.tylerwade.backend.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "SignupVerificationCode", timeToLive = 300L)
public class SignupVerificationCode {


    @Id
    private String email;
    private String verificationCode;
    private int codesSent;

    public SignupVerificationCode(String email, String verificationCode, int codesSent) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.codesSent = codesSent;
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

    public int getCodesSent() {
        return codesSent;
    }

    public void setCodesSent(int codesSent) {
        this.codesSent = codesSent;
    }
}
