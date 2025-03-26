package net.tylerwade.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.tylerwade.backend.model.dto.APIResponse;
import net.tylerwade.backend.model.dto.alert.AddAlertFieldsRequest;
import net.tylerwade.backend.model.entity.alert.Alert;
import net.tylerwade.backend.model.entity.alert.AlertConfig;
import net.tylerwade.backend.model.entity.Application;
import net.tylerwade.backend.model.entity.User;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.model.entity.alert.AlertField;
import net.tylerwade.backend.services.AlertService;
import net.tylerwade.backend.services.ApplicationService;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final ApplicationService applicationService;

    public AlertController(AlertService alertService, ApplicationService applicationService) {
        this.alertService = alertService;
        this.applicationService = applicationService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getAlertsInApplication(
            HttpServletRequest request,
            @RequestHeader String appId,
            // Params
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "50", required = false) int pageSize
    ) {
        try {
            User user = (User) request.getAttribute("user");

            // Construct pageable

            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Alert> alerts = alertService.getAlertsInApplication(appId, user.getId(), pageable);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(alerts, "Alerts Retrieved Successfully."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping({"", "/"})
    public ResponseEntity<?> clearAlertsInApplication(HttpServletRequest request, @RequestHeader String appId) {
        try {
            User user = (User) request.getAttribute("user");
            alertService.clearAlertsInApplication(appId, user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "Alerts in application cleared."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig(HttpServletRequest request, @RequestHeader String appId) {
        try {
            User user = (User) request.getAttribute("user");
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertConfig config = alertService.getOrCreateAlertConfig(app);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(config, "Alert Config Retrieved Successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/config/toggle-email-alerts")
    public ResponseEntity<?> toggleEmailAlerts(HttpServletRequest request, @RequestHeader String appId) {
        try {
            User user = (User) request.getAttribute("user");
            boolean newStatus = alertService.toggleEmailAlerts(appId, user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(newStatus, String.format("Email alerts enabled: %s", newStatus)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/config/add-field")
    public ResponseEntity<?> addFieldToAlertConfig(HttpServletRequest request, @RequestHeader String appId, @RequestBody AddAlertFieldsRequest addAlertFieldsRequest) {
        try {
            User user = (User) request.getAttribute("user");
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertField alertField = alertService.addFieldToAlertConfig(addAlertFieldsRequest, app);

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(alertField, "Alert Fields added successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/config/remove-field/{alertFieldId}")
    public ResponseEntity<?> removeFieldFromAlertConfig(HttpServletRequest request, @RequestHeader String appId, @PathVariable Long alertFieldId) {
        try {
            User user = (User) request.getAttribute("user");
            Application app = applicationService.getApplication(appId);

            // Check if owner of app
            if (!app.getUserId().equals(user.getId())) throw new UnauthorizedException("Unauthorized");

            AlertConfig config = alertService.removeFieldFromAlertConfig(alertFieldId, app);

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
