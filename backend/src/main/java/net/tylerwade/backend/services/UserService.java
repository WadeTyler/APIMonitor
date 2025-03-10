package net.tylerwade.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import net.tylerwade.backend.dto.LoginRequest;
import net.tylerwade.backend.dto.SignupRequest;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String authSecret;
    private final String environment;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, @Value("${JWT_AUTH_SECRET}") String authSecret, @Value("${ENVIRONMENT}") String environment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecret = authSecret;
        this.environment = environment;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Cookie createAuthTokenCookie(String id) {
        String authToken = createAuthToken(id);

        Cookie cookie = new Cookie("auth_token", authToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(environment.equals("production"));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 24 hours

        return cookie;
    }

    public Cookie createLogoutCookie() {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(environment.equals("production"));
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    private String createAuthToken(String id) {
        Algorithm algorithm = Algorithm.HMAC256(authSecret);
        return JWT.create()
                .withIssuer("apimonitor")
                .withSubject(id)
                .sign(algorithm);
    }

    public User attemptLogin(LoginRequest loginRequest) throws BadRequestException {
        // Check for missing fields
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()
        || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new BadRequestException("All fields required. (Email, Password)");
        }

        // Check if user exists with email
        Optional<User> existingUserOptional = userRepository.findByEmailIgnoreCase(loginRequest.getEmail());
        if (existingUserOptional.isEmpty()) {
            throw new BadRequestException("Invalid Email or Password");
        }

        // Check if passwords match
        if (!verifyPassword(loginRequest.getPassword(), existingUserOptional.get().getPassword())) {
            throw new BadRequestException("Invalid Email or Password");
        }

        return existingUserOptional.get();
    }

    public User attemptSignup(SignupRequest signupRequest) throws BadRequestException, NotAcceptableException {
        // Check for missing values
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isEmpty()
        || signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()
        || signupRequest.getConfirmPassword() == null || signupRequest.getConfirmPassword().isEmpty()) {
            throw new BadRequestException("All fields required. (Email, Password, Confirm Password)");
        }

        // Check passwords match
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }

        // Check password length
        if (signupRequest.getPassword().length() < 6 || signupRequest.getPassword().length() > 50 || signupRequest.getPassword().contains(" ")) {
            throw new BadRequestException("Password must be between 6-50 characters and cannot contain spaces.");
        }

        // Check if email already taken
        if (userRepository.existsByEmailIgnoreCase(signupRequest.getEmail())) {
            throw new NotAcceptableException("Email already taken.");
        }

        // Add new user
        User user = new User(signupRequest.getEmail(), encodePassword(signupRequest.getPassword()));
        userRepository.save(user);

        return user;
    }

    private boolean verifyPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public User getUser(String authToken) throws UnauthorizedException {
        // Decode token and obtain userId
        String userId = decodeToken(authToken);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) throw new UnauthorizedException("Unauthorized");

        return userOptional.get();
    }

    private String decodeToken(String token) throws UnauthorizedException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(authSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("apimonitor")
                    .build()
                    .verify(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
