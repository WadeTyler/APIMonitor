package net.tylerwade.backend.services.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.tylerwade.backend.config.properties.ApplicationProperties;
import net.tylerwade.backend.config.properties.JwtConfig;
import net.tylerwade.backend.entity.BlacklistedJwt;

import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.BlacklistedJwtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JwtUtil {
    private final JwtConfig jwtConfig;
    private final BlacklistedJwtRepository blacklistedJwtRepository;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public JwtUtil(JwtConfig jwtConfig, BlacklistedJwtRepository blacklistedJwtRepository, ApplicationProperties applicationProperties) {
        this.jwtConfig = jwtConfig;
        this.blacklistedJwtRepository = blacklistedJwtRepository;
        this.applicationProperties = applicationProperties;
    }

    public Cookie createAuthCookie(String id) {
        String token = createAuthToken(id);
        Cookie authCookie = new Cookie(jwtConfig.getAuthCookieName(), jwtConfig.getTokenPrefix() + token);
        authCookie.setSecure(applicationProperties.getEnvironment().equals("PRODUCTION"));
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setMaxAge((int) (jwtConfig.getTokenExpiresAfterDays() * 86400));

        return authCookie;
    }

    public Cookie createLogoutCookie() {
        Cookie authCookie = new Cookie(jwtConfig.getAuthCookieName(), "");
        authCookie.setSecure(applicationProperties.getEnvironment().equals("PRODUCTION"));
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setMaxAge(0);

        return authCookie;
    }


    private String createAuthToken(String id) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecretKey());
        return JWT.create()
                .withIssuer(jwtConfig.getTokenIssuer())
                .withSubject(id)
                .withIssuedAt(new java.util.Date())
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpiresAfterDays())))
                .sign(algorithm);
    }

    public String decodeToken(String token) throws JWTVerificationException {
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecretKey());
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getTokenIssuer())
                    .build()
                    .verify(token);
            return decodedJWT.getSubject();
    }

    public void addBlacklistedToken(String token) {
        BlacklistedJwt blacklistedJwt = new BlacklistedJwt(token);
        blacklistedJwtRepository.save(blacklistedJwt);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedJwtRepository.existsById(token);
    }

    public Cookie getAuthTokenCookieFromRequest(HttpServletRequest request) throws UnauthorizedException {
        Cookie authCookie = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(jwtConfig.getAuthCookieName())) {
                authCookie = cookie;
                break;
            }
        }

        if (authCookie == null || !authCookie.getValue().startsWith(jwtConfig.getTokenPrefix())) {
            throw new UnauthorizedException("Auth Cookie not found or invalid.");
        }

        return authCookie;
    }
}