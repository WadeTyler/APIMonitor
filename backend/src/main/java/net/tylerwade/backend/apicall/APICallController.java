package net.tylerwade.backend.apicall;

import net.tylerwade.backend.service.Service;
import net.tylerwade.backend.service.ServiceRepository;
import net.tylerwade.backend.user.User;
import net.tylerwade.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/apicalls")
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
            return new ResponseEntity<>("APICall missing", HttpStatus.BAD_REQUEST);
        }
        // Check serviceId
        if (apiCall.getServiceId() == null || apiCall.getServiceId().isEmpty()) {
            return new ResponseEntity<>("Service id missing", HttpStatus.BAD_REQUEST);
        }
        // Check serviceId is legit
        if (!serviceRepository.existsById(apiCall.getServiceId())) {
            return new ResponseEntity<>("Invalid ServiceID. Service not found.", HttpStatus.NOT_FOUND);
        }
        // Check path
        if (apiCall.getPath() == null || apiCall.getPath().isEmpty()) {
            return new ResponseEntity<>("API call path missing", HttpStatus.BAD_REQUEST);
        }
        // Check method
        if (apiCall.getMethod() == null || apiCall.getMethod().isEmpty()) {
            return new ResponseEntity<>("API call method missing", HttpStatus.BAD_REQUEST);
        }
        // Check Timestamp
        if (apiCall.getTimestamp() == null) {
            return new ResponseEntity<>("API call timestamp missing", HttpStatus.BAD_REQUEST);
        }

        // Save API Call
        apiCallRepository.save(apiCall);

        return new ResponseEntity<>(apiCall, HttpStatus.CREATED);
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
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Check if service found
        Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
        if (!serviceOptional.isPresent()) {
            return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
        }

        Service service = serviceOptional.get();

        // Check if owner of service
        if (!service.getUserId().equals(user.getId())) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
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

        return new ResponseEntity<>(apiCalls, HttpStatus.OK);
    }

}
