package net.tylerwade.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenIssuer;
    private String tokenPrefix;
    private String tokenHeader;
    private String authCookieName;
    private Long tokenExpiresAfterDays;

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Long getTokenExpiresAfterDays() {
        return tokenExpiresAfterDays;
    }

    public void setTokenExpiresAfterDays(Long tokenExpiresAfterDays) {
        this.tokenExpiresAfterDays = tokenExpiresAfterDays;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getAuthCookieName() {
        return authCookieName;
    }

    public void setAuthCookieName(String authCookieName) {
        this.authCookieName = authCookieName;
    }
}
