package net.tylerwade.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    private User validUser;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setEmail("john@doe.com");
        validUser.setPassword("securepassword");
    }

    ////////////////////
    /// SIGNUP TESTS ///
    ////////////////////

    @Test
    void shouldSignupSuccessfully() throws Exception {
        when(userService.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(userService.encodePassword(validUser.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        when(userService.createAuthTokenCookie(any())).thenReturn(new Cookie("auth_token", "fake_token"));

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(validUser.getEmail()))
                .andExpect(cookie().exists("auth_token"));
    }

    @Test
    void shouldReturnConflictIfEmailAlreadyExists() throws Exception {
        when(userService.existsByEmail(validUser.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already in use."));
    }

    @Test
    void shouldReturnBadRequestIfPasswordIsTooShort() throws Exception {
        validUser.setPassword("123");

        mockMvc.perform(post("/api/user/signup")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password must be between 6 and 50 characters."));
    }

    //////////////////////////////////////
    /// Login Tests
    //////////////////////////////////////

    @Test
    void shouldReturnOKIfLoginSuccessful() throws Exception {
        when(userService.attemptLogin(validUser.getEmail(), validUser.getPassword())).thenReturn(validUser);
        when(userService.createAuthTokenCookie(any())).thenReturn(new Cookie("auth_token", "fake_token"));

        User loginAttempt = new User();
        loginAttempt.setEmail(validUser.getEmail());
        loginAttempt.setPassword("securepassword");

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginAttempt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validUser.getEmail()))
                .andExpect(cookie().exists("auth_token"));
    }

    @Test
    void shouldReturnBadRequestIfInvalidPassword() throws Exception {
        when(userService.attemptLogin(validUser.getEmail(), validUser.getPassword())).thenReturn(null);

        User loginAttempt = new User();
        loginAttempt.setEmail(validUser.getEmail());
        loginAttempt.setPassword("securepassword");

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginAttempt)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email or password."));
    }

    //////////////////////////////////////
    /// GET ME
    //////////////////////////////////////

    @Test
    void shouldReturnBadRequestIfNoAuthToken() throws Exception {
        mockMvc.perform(get("/api/user/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserIfAuthTokenExists() throws Exception {
        when(userService.getUserFromAuthToken(any())).thenReturn(validUser);

        Cookie authToken = new Cookie("auth_token", "fake_token");

        mockMvc.perform(get("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validUser.getEmail()));
    }

    //////////////////////////////////////
    /// LOGOUT
    //////////////////////////////////////

    @Test
    void shouldReturnLogoutSuccessfulIfAuthToken() throws Exception {
        Cookie authToken = new Cookie("auth_token", "fake_token");

        // Logout cookie
        Cookie logoutCookie = new Cookie("auth_token", "fake_token");
        logoutCookie.setPath("/");
        logoutCookie.setMaxAge(0);

        when(userService.createLogoutCookie()).thenReturn(logoutCookie);

        mockMvc.perform(put("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful."))
                .andExpect(cookie().exists("auth_token"));
    }

    @Test
    void shouldReturnBadRequestIfInvalidAuthToken() throws Exception {

        mockMvc.perform(put("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
