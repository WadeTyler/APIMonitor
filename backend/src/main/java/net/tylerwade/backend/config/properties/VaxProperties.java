package net.tylerwade.backend.config.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "vax.properties")
public class VaxProperties {

    private Integer maxApiCallsFreeTier;
    private Integer maxPasswordChangeAttempts;
    private Integer maxSignupVerificationCodeAttempts;
    private Integer maxApplicationsFreeTier;

    public void setMaxApiCallsFreeTier(Integer maxApiCallsFreeTier) {
        this.maxApiCallsFreeTier = maxApiCallsFreeTier;
    }

    public Integer getMaxApiCallsFreeTier() {
        return maxApiCallsFreeTier;
    }

    public Integer getMaxPasswordChangeAttempts() {
        return maxPasswordChangeAttempts;
    }

    public void setMaxPasswordChangeAttempts(Integer maxPasswordChangeAttempts) {
        this.maxPasswordChangeAttempts = maxPasswordChangeAttempts;
    }

    public Integer getMaxSignupVerificationCodeAttempts() {
        return maxSignupVerificationCodeAttempts;
    }

    public void setMaxSignupVerificationCodeAttempts(Integer maxSignupVerificationCodeAttempts) {
        this.maxSignupVerificationCodeAttempts = maxSignupVerificationCodeAttempts;
    }

    public Integer getMaxApplicationsFreeTier() {
        return maxApplicationsFreeTier;
    }

    public void setMaxApplicationsFreeTier(Integer maxApplicationsFreeTier) {
        this.maxApplicationsFreeTier = maxApplicationsFreeTier;
    }

    // Run Checks to see if anything is missing.
    @PostConstruct
    public void validateProperties() {
        if (maxApiCallsFreeTier == null) {
            throw new IllegalStateException("Missing application properties: vax.properties.max-api-calls-free-tier");
        }

        if (maxPasswordChangeAttempts == null) {
            throw new IllegalStateException("Missing application properties: vax.properties.max-password-change-attempts");
        }

        if (maxSignupVerificationCodeAttempts == null) {
            throw new IllegalStateException("Missing application properties: vax.properties.max-signup-verification-code-attempts");
        }
        if (maxApplicationsFreeTier == null) {
            throw new IllegalStateException("Missing application properties: vax.properties.max-applications-free-tier");
        }
    }
}
