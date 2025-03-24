package net.tylerwade.backend.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.backend.config.properties.JwtConfig;
import net.tylerwade.backend.config.properties.VaxProperties;
import net.tylerwade.backend.dto.*;
import net.tylerwade.backend.entity.DeleteAccountVerificationCode;
import net.tylerwade.backend.entity.PasswordChangeAttempt;
import net.tylerwade.backend.entity.SignupVerificationCode;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.model.SecurityUser;
import net.tylerwade.backend.repository.*;
import net.tylerwade.backend.services.util.JwtUtil;
import net.tylerwade.backend.util.HTMLMessageTemplates;
import net.tylerwade.backend.util.Util;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    // Dependencies
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final APICallRepository apiCallRepository;
    private final SignupVerificationCodeRepository signupVerificationCodeRepository;
    private final PasswordChangeAttemptRepository passwordChangeAttemptRepository;
    private final DeleteAccountCodeRepository deleteAccountCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VaxProperties vaxProperties;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Autowired
    public UserService(UserRepository userRepository, ApplicationRepository applicationRepository, APICallRepository apiCallRepository, PasswordChangeAttemptRepository passwordChangeAttemptRepository, DeleteAccountCodeRepository deleteAccountCodeRepository, PasswordEncoder passwordEncoder, SignupVerificationCodeRepository signupVerificationCodeRepository, EmailService emailService, VaxProperties vaxProperties, JwtUtil jwtUtil, JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.apiCallRepository = apiCallRepository;
        this.deleteAccountCodeRepository = deleteAccountCodeRepository;
        this.signupVerificationCodeRepository = signupVerificationCodeRepository;
        this.passwordChangeAttemptRepository = passwordChangeAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.vaxProperties = vaxProperties;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
        String token = jwtUtil.getAuthTokenCookieFromRequest(request).getValue().replace(jwtConfig.getTokenPrefix(), "");
        jwtUtil.addBlacklistedToken(token);
        response.addCookie(jwtUtil.createLogoutCookie());
    }

    public User attemptSignup(SignupRequest signupRequest) throws BadRequestException, NotAcceptableException {

        checkValidSignupRequest(signupRequest);

        // Check for verification code
        if (signupRequest.getVerificationCode() == null || signupRequest.getVerificationCode().isEmpty()) {
            throw new BadRequestException("Verification Code Required.");
        }

        // Validate Signup Verification Code
        Optional<SignupVerificationCode> codeOptional = signupVerificationCodeRepository.findById(signupRequest.getEmail());

        // Check code exists and matches
        if (codeOptional.isEmpty() || (!codeOptional.get().getVerificationCode().equals(signupRequest.getVerificationCode()))) {
            throw new BadRequestException("Invalid or Expired Verification Code.");
        }

        // Add new user
        User user = new User(signupRequest.getEmail(), encodePassword(signupRequest.getPassword()));
        userRepository.save(user);

        // Delete verification code
        signupVerificationCodeRepository.delete(codeOptional.get());

        return user;
    }

    public void attemptSignupVerificationProccess(SignupRequest signupRequest) throws NotAcceptableException, BadRequestException, MessagingException {
        checkValidSignupRequest(signupRequest);

        // Create Code
        SignupVerificationCode signupVerificationCode = generateSignupVerificationCode(signupRequest.getEmail());

        // Send email with code
        emailService.sendHTMLMessage(signupRequest.getEmail(),
                "Signup Verification Code | Vax Monitor",
                HTMLMessageTemplates.getVerificationCodeTemplate(signupVerificationCode.getVerificationCode()));
    }

    // Check if signuprequest info is valid, does not check for verification code however.
    private void checkValidSignupRequest(SignupRequest signupRequest) throws NotAcceptableException, BadRequestException {
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
    }

    private SignupVerificationCode generateSignupVerificationCode(String email) throws NotAcceptableException {

        String code = Util.generateRandomVerificationCode(4);

        // Check if code already sent
        Optional<SignupVerificationCode> signupVerificationCodeOptional = signupVerificationCodeRepository.findById(email);

        if (signupVerificationCodeOptional.isEmpty()) {
            // Create a new one
            SignupVerificationCode signupVerificationCode = new SignupVerificationCode(email, code, 1);
            signupVerificationCodeRepository.save(signupVerificationCode);
            return signupVerificationCode;
        }

        SignupVerificationCode signupVerificationCode = signupVerificationCodeOptional.get();

        // Check if reached max attempts
        if (signupVerificationCode.getCodesSent() >= vaxProperties.getMaxSignupVerificationCodeAttempts()) {
            throw new NotAcceptableException("Max Signup Verification Codes Requested. Please try again later.");
        }

        // Update code and count

        signupVerificationCode.setVerificationCode(code);
        signupVerificationCode.setCodesSent(signupVerificationCode.getCodesSent() + 1);
        // Save
        signupVerificationCodeRepository.save(signupVerificationCode);

        return signupVerificationCode;
    }

    private boolean verifyPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
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
            if (pwc.getCount() >= vaxProperties.getMaxPasswordChangeAttempts()) {
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

    public void sendDeleteAccountVerificationCode(User user) throws MessagingException {
        // Code
        String code = Util.generateRandomVerificationCode(8);

        DeleteAccountVerificationCode deleteAccountVerificationCode = new DeleteAccountVerificationCode(user.getId(), code);
        deleteAccountCodeRepository.save(deleteAccountVerificationCode);

        emailService.sendHTMLMessage(user.getEmail(), "Delete Account Verification Code | Vax Monitor", HTMLMessageTemplates.getDeleteAccountVerificationCodeTemplate(code));
    }

    public void deleteAccount(DeleteAccountRequest deleteRequest, User user) throws BadRequestException, UnauthorizedException {
        // Check for missing fields
        if (deleteRequest.getPassword() == null || deleteRequest.getPassword().isEmpty()
                || deleteRequest.getVerificationCode() == null || deleteRequest.getVerificationCode().isEmpty()) {
            throw new BadRequestException("All fields required: (Password, Verification Code)");
        }

        // Check if valid code
        Optional<DeleteAccountVerificationCode> deleteAccountVerificationCodeOptional = deleteAccountCodeRepository.findById(user.getId());
        if (deleteAccountVerificationCodeOptional.isEmpty()) {
            throw new BadRequestException("Verification Code is Invalid or Expired.");
        }

        DeleteAccountVerificationCode deleteAccountVerificationCode = deleteAccountVerificationCodeOptional.get();

        if (!deleteAccountVerificationCode.getVerificationCode().equals(deleteRequest.getVerificationCode())) {
            throw new BadRequestException("Verification Code is Invalid or Expired.");
        }

        // Check if password is correct
        if (!verifyPassword(deleteRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Password is incorrect.");
        }

        // Delete code
        deleteAccountCodeRepository.delete(deleteAccountVerificationCode);

        // Delete account
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Username %s not found.", username));
        }
        return new SecurityUser(user.get());
    }

    public SecurityUser loadUserByUserId(String userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User not found by id: %s", userId));
        }

        return new SecurityUser(user.get());
    }
}








