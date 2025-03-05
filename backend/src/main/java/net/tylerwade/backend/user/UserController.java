package net.tylerwade.backend.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.backend.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;
    UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletResponse response, @RequestBody User user) {

        // Validate User
        if (
                user.getEmail() == null ||
                        user.getEmail().isEmpty() ||
                        user.getPassword() == null ||
                        user.getPassword().isEmpty() ||
                        user.getFirstName() == null ||
                        user.getFirstName().isEmpty() ||
                        user.getLastName() == null ||
                        user.getLastName().isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("All fields required."));
        }

        // Check email taken
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Email already taken."));
        }

        // Check password in between 10 and 50 char
        if (user.getPassword().length() < 6 || user.getPassword().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Password must be between 6 and 50 characters."));
        }

        // Encode password
        user.setPassword(userService.encodePassword(user.getPassword()));

        // Save user
        userRepository.save(user);

        // Generate cookie
        try {
            Cookie authTokenCookie = userService.createAuthTokenCookie(user.getId());
            // Add cookie
            response.addCookie(authTokenCookie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Internal Server Error. Failed to create Auth Token"));
        }

        // return the new user after removing password
        user.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(user, "Signup Successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody User loginAttempt) {

        // Check for email and password
        if (loginAttempt.getEmail() == null
                || loginAttempt.getEmail().isEmpty()
                || loginAttempt.getPassword() == null
                || loginAttempt.getPassword().isEmpty()
        ) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("All fields required."));
        }

        // Find by email and validate
        User user = userService.attemptLogin(loginAttempt.getEmail(), loginAttempt.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid email or password."));
        }

        // Attach authtoken
        try {
            Cookie authToken = userService.createAuthTokenCookie(user.getId());
            response.addCookie(authToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to create Auth Token."));
        }

        // Return user
        user.setPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(user, "Login Successful"));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getMe(@CookieValue(name = "auth_token") String authToken) {

        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized."));
        }
        // Return user
        user.setPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(user, "User info retrieved successfully."));
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue("auth_token") String authToken) {
        response.addCookie(userService.createLogoutCookie());
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Logout Successful"));
    }
}
