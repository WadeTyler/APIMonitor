package net.tylerwade.backend.controller;

import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.dto.CreateApplicationRequest;
import net.tylerwade.backend.entity.Application;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.services.ApplicationService;
import net.tylerwade.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public ApplicationController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplication(@CookieValue("auth_token") String authToken, @RequestBody CreateApplicationRequest createApplicationRequest) {
        try {
            User user = userService.getUser(authToken);
            Application application = applicationService.createApplication(createApplicationRequest, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(application, "Application created."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{publicToken}")
    public ResponseEntity<?> getApplicationFromPublicToken(@CookieValue("auth_token") String authToken, @PathVariable String publicToken) {
        try {
            User user = userService.getUser(authToken);
            Application application = applicationService.getApplicationFromPublicToken(publicToken, user.getId());
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(application, "Application Retrieved."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteApplication(@RequestHeader String appId, @CookieValue("auth_token") String authToken) {
        try {
            User user = userService.getUser(authToken);
            applicationService.deleteApplication(appId, user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Application deleted."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getAllApplications(@CookieValue("auth_token") String authToken) {
        try {
            User user = userService.getUser(authToken);
            List<Application> applications = applicationService.getAllApplications(user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(applications, "User applications received successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        }
    }

}
