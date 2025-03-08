package net.tylerwade.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.dto.LoginRequest;
import net.tylerwade.backend.dto.SignupRequest;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletResponse response, @RequestBody SignupRequest signupRequest) {
        try {
            // Attempt to signup user
            User user = userService.attemptSignup(signupRequest);

            // Generate Cookie
            Cookie authToken = userService.createAuthTokenCookie(user.getId());
            response.addCookie(authToken);

            user.setPassword(null);

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(user, "User created Successfully."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Something went wrong. Try again later."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.attemptLogin(loginRequest);

            // Create Authtoken
            Cookie authToken = userService.createAuthTokenCookie(user.getId());
            response.addCookie(authToken);

            user.setPassword(null);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(user, "User Login Successful"));
        } catch (BadRequestException e) {
            Cookie logoutAuthToken = userService.createLogoutCookie();
            response.addCookie(logoutAuthToken);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            Cookie logoutAuthToken = userService.createLogoutCookie();
            response.addCookie(logoutAuthToken);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Something went wrong. Try again later."));
        }
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getMe(HttpServletResponse response, @CookieValue(name = "auth_token") String authToken) {
        try {
            User user = userService.getUser(authToken);
            user.setPassword(null);
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(user, "User Data Retrieved."));
        } catch (UnauthorizedException e) {
            Cookie logoutAuthToken = userService.createLogoutCookie();
            response.addCookie(logoutAuthToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(userService.createLogoutCookie());
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Logout Successful"));
    }
}
