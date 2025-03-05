package net.tylerwade.backend.service;

import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.user.User;
import net.tylerwade.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController @RequestMapping("/api/services")
public class ServiceController {

    UserService userService;
    ServiceRepository serviceRepository;

    public ServiceController(UserService userService, ServiceRepository serviceRepository) {
        this.userService = userService;
        this.serviceRepository = serviceRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createService(@RequestBody Service service, @CookieValue("auth_token") String authToken) {

        User user = userService.getUserFromAuthToken(authToken);
        // Validate user
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        service.setUserId(user.getId());

        // Validate name
        if (service.getName() == null || service.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Service name required."));
        }

        if (serviceRepository.existsByNameAndUserIdIgnoreCase(service.getName(), service.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("You already have a service with that name."));
        }

        // Save service
        serviceRepository.save(service);

        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(service, "Service Created Successfully"));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable String serviceId, @CookieValue("auth_token") String authToken) {
        // Validate auth token
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Check service exists
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        if (serviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Service not found."));
        }

        // Check if owner of service
        if (!serviceOptional.get().getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Delete Service
        serviceRepository.deleteById(serviceId);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Service Deleted Successfully"));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getAllServices(@CookieValue("auth_token") String authToken) {
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        List<Service> services = serviceRepository.findAllByUserId(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(services, "Services Retrieved Successfully"));
    }

}
