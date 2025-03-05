package net.tylerwade.backend.apicall;

import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.service.Service;
import net.tylerwade.backend.service.ServiceRepository;
import net.tylerwade.backend.user.User;
import net.tylerwade.backend.user.UserService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apicalls")
public class APICallController {

    private final APICallRepository apiCallRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;

    public APICallController(APICallRepository apiCallRepository, ServiceRepository serviceRepository, UserService userService) {
        this.apiCallRepository = apiCallRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<?> addApiCall(@RequestBody APICall apiCall) {

        // Validate api call
        if (apiCall == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("API call missing"));
        }
        // Check serviceId
        if (apiCall.getServiceId() == null || apiCall.getServiceId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Service ID missing"));
        }
        // Check serviceId is legit
        if (!serviceRepository.existsById(apiCall.getServiceId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Service Not Found"));
        }
        // Check path
        if (apiCall.getPath() == null || apiCall.getPath().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("API call path missing"));
        }
        // Check method
        if (apiCall.getMethod() == null || apiCall.getMethod().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("API call method missing"));
        }
        // Check Timestamp
        if (apiCall.getTimestamp() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("API call timestamp missing"));
        }

        // Save API Call
        apiCallRepository.save(apiCall);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(apiCall, "API Call added successfully"));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getAllApiCallsInService(
            @RequestHeader("serviceId") String serviceId,
            @CookieValue("auth_token") String authToken,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String method
    ) {
        // Validate user
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Check if service found
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        if (!serviceOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Service not found"));
        }

        Service service = serviceOptional.get();

        // Check if owner of service
        if (!service.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        List<APICall> apiCalls = apiCallRepository.findAllByServiceId(serviceId);

        // Filter by path
        if (path != null && !path.isEmpty()) {
            apiCalls = apiCalls.stream().filter(apiCall -> apiCall
                            .getPath().toLowerCase()
                            .contains(path.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by method
        if (method != null && !method.isEmpty()) {
            apiCalls = apiCalls.stream().filter(apiCall -> apiCall
                            .getMethod().toLowerCase()
                            .contains(method.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(apiCalls, "API Calls retrieved successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApiCall(@PathVariable("id") Long id,
                                           @RequestHeader("serviceId") String serviceId,
                                           @CookieValue("auth_token") String authToken
    ) {
        // Validate User
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Verify service exists
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        if (serviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Service not found."));
        }

        Service service = serviceOptional.get();

        // Verify owner of service
        if (!service.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Verify API Call exists and is in service
        Optional<APICall> apiCallOptional = apiCallRepository.findById(id);
        if (apiCallOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("API Call not found."));
        }

        // Verify API Call is in service
        if (!apiCallOptional.get().getServiceId().equals(serviceId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Delete the api call
        apiCallRepository.delete(apiCallOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "API Call Deleted."));
    }

    @DeleteMapping({"/", ""})
    public ResponseEntity<?> deleteAllApiCallsInService(
            @RequestHeader String serviceId,
            @CookieValue("auth_token") String authToken
    ) {
        // Validate user
        User user = userService.getUserFromAuthToken(authToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        // Verify service exists
        if (serviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Service not found."));
        }

        Service service = serviceOptional.get();
        // Verify owner of service
        if (!service.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized"));
        }

        // Delete all api calls in service
        apiCallRepository.deleteAllByServiceId(serviceId);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "All API Calls in service deleted successfully."));
    }

}
