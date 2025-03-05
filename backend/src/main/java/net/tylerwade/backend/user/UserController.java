package net.tylerwade.backend.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            return new ResponseEntity<>("All fields required.", HttpStatus.BAD_REQUEST);
        }

        // Check email taken
        if (userService.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("Email already in use.", HttpStatus.CONFLICT);
        }

        // Check password in between 10 to 50 char
        if (user.getPassword().length() < 6 || user.getPassword().length() > 50) {
            return new ResponseEntity<>("Password must be between 6 and 50 characters.", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("Failed to create auth token.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // return the new user after removing password
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody User loginAttempt) {

        // Check for email and password
        if (loginAttempt.getEmail() == null
                || loginAttempt.getEmail().isEmpty()
                || loginAttempt.getPassword() == null
                || loginAttempt.getPassword().isEmpty()
        ) {
         return new ResponseEntity<>("All fields required.", HttpStatus.BAD_REQUEST);
        }

        // Find by email and validate
        User user = userService.attemptLogin(loginAttempt.getEmail(), loginAttempt.getPassword());

        if (user == null) {
            return new ResponseEntity<>("Invalid email or password.", HttpStatus.BAD_REQUEST);
        }

        // Attach authtoken
        try {
            Cookie authToken = userService.createAuthTokenCookie(user.getId());
            response.addCookie(authToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create auth token.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Return user
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getMe(@CookieValue(name = "auth_token") String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            return new ResponseEntity<>("Auth token is required.", HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return new ResponseEntity<>("Invalid AuthToken", HttpStatus.BAD_REQUEST);
        }
        // Return user
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue("auth_token") String authToken) {
        response.addCookie(userService.createLogoutCookie());
        return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
    }
}
