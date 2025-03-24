package net.tylerwade.backend.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.backend.dto.*;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.services.UserService;
import net.tylerwade.backend.services.util.JwtUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup/verify")
    public ResponseEntity<?> signupVerification(HttpServletResponse response, @RequestBody SignupRequest signupRequest) {
        try {
            // Attempt to signup user
            User user = userService.attemptSignup(signupRequest);

            // Authenticate user
            Cookie authCookie = jwtUtil.createAuthCookie(user.getId());
            response.addCookie(authCookie);

            UserDTO userDTO = userService.convertToUserDTO(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(userDTO, "User created Successfully."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.out.println("Exception caught: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Something went wrong. Try again later."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> receiveSignupVerificationCode(@RequestBody SignupRequest signupRequest) {
        try {
            userService.attemptSignupVerificationProccess(signupRequest);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Please check your email for a verification code."));
        } catch (MessagingException e) {
            System.out.println("Signup Verification MessagingException caught: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Something went wrong. Check the email field, or try again later."));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }


    @GetMapping({"/", ""})
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        UserDTO userDTO = userService.convertToUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(userDTO, "Successfully retrieved user details."));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");

        // Convert to User Profile DTO
        UserProfileDTO userProfileDTO = userService.convertToUserProfileDTO(user);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(userProfileDTO, "User Profile Retrieved"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest changePasswordRequest, HttpServletResponse response) {
        try {
            User user = (User) request.getAttribute("user");

            // Change Password
            userService.changePassword(changePasswordRequest, user);

            userService.logoutUser(request, response);

            // Return message
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Password Changed Successfully. Please login again."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.logoutUser(request, response);
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Logout Successful"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account/verify")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request, @RequestBody DeleteAccountRequest deleteRequest, HttpServletResponse response) {
        try {
            // Get User
            User user = (User) request.getAttribute("user");

            // Attempt to delete account
            userService.deleteAccount(deleteRequest, user);

            userService.logoutUser(request, response);

            // Return deleted
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Account deleted successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/delete-account/send-code")
    public ResponseEntity<?> sendDeleteAccountVerificationCode(HttpServletRequest request) {
        try {
            // Get User
            User user = (User) request.getAttribute("user");

            // Send verification code to email
            userService.sendDeleteAccountVerificationCode(user);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "An email has been sent with a verification to delete your account."));
        } catch (MessagingException e) {
            System.out.println("DeleteAccount MessagingException caught: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Something went wrong. Check the email field, or try again later."));
        }
    }
}
