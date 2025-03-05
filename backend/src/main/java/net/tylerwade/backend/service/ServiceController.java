package net.tylerwade.backend.service;

import net.tylerwade.backend.user.User;
import net.tylerwade.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity<>("Invalid AuthToken", HttpStatus.UNAUTHORIZED);
        }

        service.setUserId(user.getId());

        // Validate name
        if (service.getName() == null || service.getName().isEmpty()) {
            return new ResponseEntity<>("Service name is required.", HttpStatus.BAD_REQUEST);
        }

        if (serviceRepository.existsByNameAndUserIdIgnoreCase(service.getName(), service.getUserId())) {
            return new ResponseEntity<>("You already have a service with the name: " + service.getName(), HttpStatus.CONFLICT);
        }

        // Save service
        serviceRepository.save(service);

        return new ResponseEntity<>(service, HttpStatus.CREATED);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable String serviceId, @CookieValue("auth_token") String authToken) {
        // Validate auth token
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return new ResponseEntity<>("Invalid AuthToken", HttpStatus.BAD_REQUEST);
        }

        // Check service exists
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        if (serviceOptional.isEmpty()) {
            return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
        }

        // Check if owner of service
        if (!serviceOptional.get().getUserId().equals(user.getId())) {
            return new ResponseEntity<>("You do not have permission to delete this service", HttpStatus.UNAUTHORIZED);
        }

        // Delete Service
        serviceRepository.deleteById(serviceId);

        return new ResponseEntity<>("Service deleted", HttpStatus.OK);
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getAllServices(@CookieValue("auth_token") String authToken) {
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return new ResponseEntity<>("Invalid AuthToken", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(serviceRepository.findAllByUserId(user.getId()), HttpStatus.OK);
    }

}
