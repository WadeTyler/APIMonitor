package net.tylerwade.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import net.tylerwade.backend.user.User;
import net.tylerwade.backend.user.UserService;
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
@WebMvcTest(ServiceController.class)
public class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ServiceRepository serviceRepository;

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
        when(userService.getUserFromAuthToken(any())).thenReturn(null);

        Cookie fakeAuthToken = new Cookie("auth_token", "fake_token");

        Service service = new Service(validUser.getId(), "Sample Service Name");

        // Act & Assert
        mockMvc.perform(
                        post("/api/services/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(fakeAuthToken)
                                .content(objectMapper.writeValueAsString(service)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void CreateServiceShouldReturnService() throws Exception {
        // Arrange
        when(userService.getUserFromAuthToken(any())).thenReturn(validUser);
        Cookie authToken = new Cookie("auth_token", "fake_token");
        Service service = new Service(validUser.getId(), "Sample Service Name");
        when(serviceRepository.existsByNameAndUserIdIgnoreCase(any(), any())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(
                        post("/api/services/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(service))
                                .cookie(authToken))
                .andExpect(jsonPath("$.name").value(service.getName()))
                .andExpect(jsonPath("$.userId").value(service.getUserId())
                );
    }

    /// Delete Service

    @Test
    void DeleteServiceShouldReturnUnauthorizedOnNotOwner() throws Exception {
        // Arrange
        when(userService.getUserFromAuthToken(any())).thenReturn(validUser);
        Service service = new Service("random_id", "Sample Service Name");
        when(serviceRepository.findById(any())).thenReturn(Optional.of(service));
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
        when(userService.getUserFromAuthToken(any())).thenReturn(validUser);
        Service service = new Service(validUser.getId(), "Sample Service Name");
        when(serviceRepository.findById(any())).thenReturn(Optional.of(service));
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
