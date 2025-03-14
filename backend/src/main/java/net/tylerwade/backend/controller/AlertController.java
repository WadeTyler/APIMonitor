package net.tylerwade.backend.controller;

import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.dto.alert.AddAlertFieldsRequest;
import net.tylerwade.backend.entity.alert.AlertConfig;
import net.tylerwade.backend.entity.Application;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.services.AlertService;
import net.tylerwade.backend.services.ApplicationService;
import net.tylerwade.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final UserService userService;
    private final AlertService alertService;
    private final ApplicationService applicationService;

    public AlertController(UserService userService, AlertService alertService, ApplicationService applicationService) {
        this.userService = userService;
        this.alertService = alertService;
        this.applicationService = applicationService;
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig(@CookieValue("auth_token") String authToken, @RequestHeader String appId) {
        try {
            User user = userService.getUser(authToken);
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertConfig config = alertService.getOrCreateAlertConfig(appId);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(config, "Alert Config Retrieved Successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/config/add-field")
    public ResponseEntity<?> addFieldToAlertConfig(@CookieValue("auth_token") String authToken, @RequestHeader String appId, @RequestBody AddAlertFieldsRequest addAlertFieldsRequest) {
        try {
            User user = userService.getUser(authToken);
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertConfig config = alertService.addFieldToAlertConfig(addAlertFieldsRequest, appId);

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(config, "Alert Fields added successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/config/remove-field/{alertFieldId}")
    public ResponseEntity<?> removeFieldFromAlertConfig(@CookieValue("auth_token") String authToken, @RequestHeader String appId, @PathVariable Long alertFieldId) {
        try {
            User user = userService.getUser(authToken);
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertConfig config = alertService.removeFieldFromAlertConfig(alertFieldId, appId);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(config, "Field removed if existed."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

}
