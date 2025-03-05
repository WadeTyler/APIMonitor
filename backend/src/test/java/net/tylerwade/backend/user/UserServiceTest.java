package net.tylerwade.backend.user;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setEmail("john@doe.com");
        validUser.setPassword("securepassword");
    }

    @Test
    void AttemptLoginShouldReturnNullOnUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Act
        User response = userService.attemptLogin("fakeemail@gmail.com", "123456");

        // Assert
        assertEquals(response, null);
    }

    @Test
    void AttemptLoginShouldReturnNullOnInvalidPassword() {
        // Arrange
        String encodedPassword = userService.encodePassword("random_password");

        // DB User, change password to encoded
        User dbUser = new User(validUser.getFirstName(), validUser.getLastName(), validUser.getEmail(), encodedPassword);
        Optional<User> dbUserOptional = Optional.of(dbUser);

        // Return dbUser when findByEmail
        when(userRepository.findByEmail(any())).thenReturn(dbUserOptional);

        // Act
        User response = userService.attemptLogin(validUser.getEmail(), validUser.getPassword());

        // Assert
        assertNull(response);
    }

    @Test
    void AttemptLoginShouldReturnUser() {
        // Arrange
        String encodedPassword = userService.encodePassword(validUser.getPassword());

        // DB User, change password to encoded
        User dbUser = new User(validUser.getFirstName(), validUser.getLastName(), validUser.getEmail(), encodedPassword);
        Optional<User> dbUserOptional = Optional.of(dbUser);

        // Return dbUser when findByEmail
        when(userRepository.findByEmail(any())).thenReturn(dbUserOptional);

        // Act
        User response = userService.attemptLogin(validUser.getEmail(), validUser.getPassword());

        // Assert
        assertNotNull(response);
        assertEquals(response.getPassword(), null);
        assertEquals(response.getEmail(), validUser.getEmail());
        assertEquals(response.getFirstName(), validUser.getFirstName());
    }

    @Test
    void createAuthTokenCookieShouldReturnCookie() throws Exception {
        // Arrange
        String id = "123";

        // Act
        Cookie response = userService.createAuthTokenCookie(id);

        // Assert
        assertNotNull(response);
        assertEquals(response.getMaxAge(), 60 * 60 * 24);   // Check age
        assertEquals(response.getPath(), "/");
        assertEquals(response.getName(), "auth_token");
        assertEquals(response.getSecure(), true);
        assertNotEquals(response.getValue(), id);
    }

    @Test
    void createLogoutCookieShouldReturnEmptyCookie() {
        // Act
        Cookie response = userService.createLogoutCookie();

        // Assert
        assertNotNull(response);
        assertEquals(response.getName(), "auth_token");
        assertEquals(response.getValue(), "");
        assertEquals(response.getMaxAge(), 0);
        assertEquals(response.getSecure(), true);
        assertEquals(response.getPath(), "/");
    }

}
