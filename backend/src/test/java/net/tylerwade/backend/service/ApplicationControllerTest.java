package net.tylerwade.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import net.tylerwade.backend.controller.ApplicationController;
import net.tylerwade.backend.entity.Application;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.repository.ApplicationRepository;
import net.tylerwade.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ApplicationRepository applicationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId("123");
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setEmail("john@doe.com");
        validUser.setPassword("securepassword");
    }

    /// Create Tests

    @Test
    void CreateServiceShouldReturnBadRequestorInvalidUser() throws Exception {
        // Arrange
        when(userService.findUserByAuthToken(any())).thenReturn(null);

        Cookie fakeAuthToken = new Cookie("auth_token", "fake_token");

        Application application = new Application(validUser.getId(), "Sample Service Name");

        // Act & Assert
        mockMvc.perform(
                        post("/api/services/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(fakeAuthToken)
                                .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void CreateServiceShouldReturnService() throws Exception {
        // Arrange
        when(userService.findUserByAuthToken(any())).thenReturn(validUser);
        Cookie authToken = new Cookie("auth_token", "fake_token");
        Application application = new Application(validUser.getId(), "Sample Service Name");
        when(applicationRepository.existsByNameAndUserIdIgnoreCase(any(), any())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(
                        post("/api/services/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(application))
                                .cookie(authToken))
                .andExpect(jsonPath("$.name").value(application.getName()))
                .andExpect(jsonPath("$.userId").value(application.getUserId())
                );
    }

    /// Delete Service

    @Test
    void DeleteServiceShouldReturnUnauthorizedOnNotOwner() throws Exception {
        // Arrange
        when(userService.findUserByAuthToken(any())).thenReturn(validUser);
        Application application = new Application("random_id", "Sample Service Name");
        when(applicationRepository.findById(any())).thenReturn(Optional.of(application));
        Cookie authToken = new Cookie("auth_token", "fake_token");

        // Act & Assert
        mockMvc.perform(
                        delete("/api/services/random_id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(authToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("You do not have permission to delete this service")
                );
    }

    @Test
    void DeleteServiceShouldReturnDeleted() throws Exception {
        // Arrange
        when(userService.findUserByAuthToken(any())).thenReturn(validUser);
        Application application = new Application(validUser.getId(), "Sample Service Name");
        when(applicationRepository.findById(any())).thenReturn(Optional.of(application));
        Cookie authToken = new Cookie("auth_token", "fake_token");

        // Act & Assert
        mockMvc.perform(
                        delete("/api/services/random_id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Service deleted")
                );
    }
}
