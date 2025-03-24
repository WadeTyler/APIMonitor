package net.tylerwade.backend.model.dto;

public class DeleteAccountRequest {

    private String verificationCode;
    private String password;

    public DeleteAccountRequest() {
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
