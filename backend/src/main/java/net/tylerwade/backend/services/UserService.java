package net.tylerwade.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import net.tylerwade.backend.dto.*;
import net.tylerwade.backend.entity.PasswordChangeAttempt;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.APICallRepository;
import net.tylerwade.backend.repository.ApplicationRepository;
import net.tylerwade.backend.repository.PasswordChangeAttemptRepository;
import net.tylerwade.backend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    // Dependencies
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final APICallRepository apiCallRepository;
    private final PasswordChangeAttemptRepository passwordChangeAttemptRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String authSecret;
    private final String environment;

    // Variables
    private final int MAX_PASSWORD_CHANGE_ATTEMPTS = 5;

    public UserService(UserRepository userRepository, ApplicationRepository applicationRepository, APICallRepository apiCallRepository, PasswordChangeAttemptRepository passwordChangeAttemptRepository, BCryptPasswordEncoder passwordEncoder, @Value("${JWT_AUTH_SECRET}") String authSecret, @Value("${ENVIRONMENT}") String environment) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.apiCallRepository = apiCallRepository;
        this.passwordChangeAttemptRepository = passwordChangeAttemptRepository;
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

        // Check password requirements
        checkPasswordRequirements(signupRequest.getPassword());

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

    public UserProfileDTO convertToUserProfileDTO(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                getApplicationCount(user.getId()),
                getTotalAPIRequests(user.getId()),
                getTotalUniqueRemoteAddresses(user.getId())
        );
    }

    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(user.getId());
    }

    private Integer getApplicationCount(String id) {
        return applicationRepository.countByUserId(id);
    }

    private Long getTotalAPIRequests(String userId) {
        Long totalAPIRequests = 0L;

        for (String appId : applicationRepository.findApplicationIdByUserId(userId)) {
            totalAPIRequests += apiCallRepository.countDistinctAPICallByAppId(appId);
        }

        return totalAPIRequests;
    }

    private Long getTotalUniqueRemoteAddresses(String userId) {
        Long totalUniqueRemoteAddresses = 0L;

        for (String appId : applicationRepository.findApplicationIdByUserId(userId)) {
            totalUniqueRemoteAddresses += apiCallRepository.countDistinctAPICallByRemoteAddressAndAppIdEquals(appId);
        }

        return totalUniqueRemoteAddresses;
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

    private void checkPasswordRequirements(String password) throws BadRequestException {
        if (password.length() < 6 || password.length() > 50) {
            throw new BadRequestException("Password length must be between 6 - 50 characters.");
        }
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) throws UnauthorizedException, BadRequestException, NotAcceptableException {
        // Add password change attempt
        Optional<PasswordChangeAttempt> pwcAttempt = passwordChangeAttemptRepository.findById(user.getId());
        if (pwcAttempt.isEmpty()) {
            // First attempt: add it.
            passwordChangeAttemptRepository.save(new PasswordChangeAttempt(user.getId(), 1));
            System.out.println("PWC Added");
        } else {
            // Not first attempt: Check if at max or add 1
            PasswordChangeAttempt pwc = pwcAttempt.get();
            // Check if at max
            if (pwc.getCount() >= MAX_PASSWORD_CHANGE_ATTEMPTS) {
                throw new NotAcceptableException("You have reached the max amount of password change attempts. Try again later.");
            }

            // Add 1
            pwc.setCount(pwc.getCount() + 1);
            passwordChangeAttemptRepository.save(pwc);
        }


        // Check for missing fields
        if (changePasswordRequest.getCurrentPassword() == null || changePasswordRequest.getCurrentPassword().isEmpty()
                || changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isEmpty()
                || changePasswordRequest.getConfirmNewPassword() == null || changePasswordRequest.getConfirmNewPassword().isEmpty())
            throw new BadRequestException("All fields required: (Current Password, New Password, Confirm New Password)");

        // Verify currentPassword
        if (!verifyPassword(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current Password is incorrect.");
        }

        // Check new password matches
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new BadRequestException("New Password does not match.");
        }

        // Check if new password is current password
        if (verifyPassword(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New Password cannot be Current Password.");
        }

        // Check new password requirements
        checkPasswordRequirements(changePasswordRequest.getNewPassword());

        // Encode and change
        user.setPassword(encodePassword(changePasswordRequest.getNewPassword()));

        // Save updated user
        userRepository.save(user);
    }
}
