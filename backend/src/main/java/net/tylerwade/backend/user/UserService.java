package net.tylerwade.backend.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private String authSecret;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, @Value("${JWT_AUTH_SECRET}") String authSecret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecret = authSecret;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Cookie createAuthTokenCookie(String id) throws Exception {
        String authToken = createAuthToken(id);

        Cookie cookie = new Cookie("auth_token", authToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24 hours

        return cookie;
    }

    public Cookie createLogoutCookie() {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    private String createAuthToken(String id) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(authSecret);
            String token = JWT.create()
                    .withIssuer("apimonitor")
                    .withSubject(id)
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw e;
        }
    }

    public User attemptLogin(String email, String password) {
        // Find by email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Not found
        if (optionalUser.isEmpty()) {
            return null;
        }

        // Verify password
        if (!verifyPassword(password, optionalUser.get().getPassword())) {
            return null;
        }

        // Remove password then return user
        User user = optionalUser.get();
        user.setPassword(null);
        return user;
    }

    private boolean verifyPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public User getUserFromAuthToken(String authToken) {
        // Decode token and obtain userId
        String userId = null;
        try {
            userId = decodeToken(authToken);
        } catch (Exception e) {
            return null;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        return user;

    }

    private String decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(authSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("apimonitor")
                    .build()
                    .verify(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            throw e;
        }
    }
}
