package net.tylerwade.backend.services;

import net.tylerwade.backend.model.dto.alert.AddAlertFieldsRequest;
import net.tylerwade.backend.model.entity.APICall;
import net.tylerwade.backend.model.entity.Application;
import net.tylerwade.backend.model.entity.User;
import net.tylerwade.backend.model.entity.alert.Alert;
import net.tylerwade.backend.model.entity.alert.AlertConfig;
import net.tylerwade.backend.model.entity.alert.AlertField;
import net.tylerwade.backend.model.entity.alert.RecentlySentAppAlerts;
import net.tylerwade.backend.repository.AlertConfigRepository;
import net.tylerwade.backend.repository.AlertRepository;
import net.tylerwade.backend.repository.ApplicationRepository;
import net.tylerwade.backend.repository.UserRepository;
import net.tylerwade.backend.repository.redis.RecentlySentAppAlertsRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final AlertConfigRepository alertConfigRepo;
    private final AlertRepository alertRepo;
    private final RecentlySentAppAlertsRepository recentlySentAppAlertsRepo;

    @Autowired
    public AlertService(EmailService emailService, UserRepository userRepository, ApplicationRepository applicationRepository, AlertConfigRepository alertConfigRepo, AlertRepository alertRepo, RecentlySentAppAlertsRepository recentlySentAppAlertsRepo) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.alertConfigRepo = alertConfigRepo;
        this.alertRepo = alertRepo;
        this.recentlySentAppAlertsRepo = recentlySentAppAlertsRepo;
    }

    /// ALERTS

    // TODO: Convert to pageable
    public Page<Alert> getAlertsInApplication(String appId, String userId, Pageable pageable) throws BadRequestException {

        Application app = applicationRepository.findByIdAndUserId(appId, userId)
                .orElseThrow(() -> new BadRequestException("Invalid appId."));

        return alertRepo.findByAppId(app.getId(), pageable);
    }

    public void clearAlertsInApplication(String appId, String userId) throws BadRequestException {
        Application app = applicationRepository.findByIdAndUserId(appId, userId)
                .orElseThrow(() -> new BadRequestException("Invalid appId."));

        alertRepo.deleteAlertsByAppId(app.getId());
    }

    ///
    ///  ALERT CONFIGS
    ///

    public AlertConfig getOrCreateAlertConfig(Application app) {

        Optional<AlertConfig> alertConfigOptional = alertConfigRepo.findByApp(app);
        if (alertConfigOptional.isEmpty()) {
            // Create a new Config
            AlertConfig config = new AlertConfig(app);
            alertConfigRepo.save(config);
            return config;
        } else {
            return alertConfigOptional.get();
        }
    }


    private boolean checkFieldMatch(AlertField field, APICall apiCall) {

        // Check fields
        if (field.getMethod() != null && !field.getMethod().equals(apiCall.getMethod())) {
            return false;
        }

        if (field.getPath() != null && !field.getPath().equals(apiCall.getPath())) {
            return false;
        }

        if (field.getResponseStatus() != null && !field.getResponseStatus().equals(apiCall.getResponseStatus())) {
            return false;
        }

        if (field.getRemoteAddress() != null && !field.getRemoteAddress().equals(apiCall.getRemoteAddress())) {
            return false;
        }

        return true;
    }

    public AlertField addFieldToAlertConfig(AddAlertFieldsRequest request, Application app) throws BadRequestException {
        // Check for at least one field
        if (request.getPath() == null && request.getMethod() == null && request.getRemoteAddress() == null && request.getResponseStatus() == null) {
            throw new BadRequestException("At least one field required: Path, Method, Remote Address, Response Status");
        }

        AlertConfig config = getOrCreateAlertConfig(app);
        List<AlertField> alertFields = config.getAlertFields();

        AlertField newAlertField = new AlertField();

        // Set Fields
        if (request.getPath() != null) {
            newAlertField.setPath(request.getPath());
        }
        if (request.getMethod() != null) {
            newAlertField.setMethod(request.getMethod());
        }
        if (request.getResponseStatus() != null) {
            newAlertField.setResponseStatus(request.getResponseStatus());
        }
        if (request.getRemoteAddress() != null) {
            newAlertField.setRemoteAddress(request.getRemoteAddress());
        }

        // Add new fields
        alertFields.add(newAlertField);

        // Update and save
        config.setAlertFields(alertFields);
        alertConfigRepo.save(config);

        return newAlertField;
    }

    public AlertConfig removeFieldFromAlertConfig(Long alertFieldsId, Application app) throws BadRequestException {
        // Check if null
        if (alertFieldsId == null) throw new BadRequestException("Alert Fields ID is required.");

        AlertConfig config = getOrCreateAlertConfig(app);

        // Filter out ID
        config.getAlertFields().removeIf(field -> field.getId().equals(alertFieldsId));

        // Update and Save
        alertConfigRepo.save(config);
        return config;
    }

    /// SEND ALERTS

    public boolean toggleEmailAlerts(String appId, String userId) throws BadRequestException {
        AlertConfig config = alertConfigRepo.findByAppIdAndUserId(appId, userId)
                .orElseThrow(() -> new BadRequestException("Invalid appId."));

        config.setEmailAlertsEnabled(!config.isEmailAlertsEnabled());
        alertConfigRepo.save(config);
        return config.isEmailAlertsEnabled();
    }

    public void sendAlerts(APICall apiCall) {

        // Find User
        Optional<User> userOptional = userRepository.findByAppId(apiCall.getAppId());
        if (userOptional.isEmpty()) return;
        User user = userOptional.get();

        // Find Alert Config
        Optional<AlertConfig> alertConfigOptional = alertConfigRepo.findByAppIdAndUserId(apiCall.getAppId(), user.getId());
        if (alertConfigOptional.isEmpty()) return;
        AlertConfig config = alertConfigOptional.get();

        Optional<Application> appOptional = applicationRepository.findById(apiCall.getAppId());
        if (appOptional.isEmpty()) return;
        Application app = appOptional.get();

        // Check all fields
        for (AlertField alertField : config.getAlertFields()) {
            if (checkFieldMatch(alertField, apiCall)) {
                // Add to alerts
                alertRepo.save(new Alert(app, alertField, apiCall));

                // If we haven't sent an alert email in the last 10 minutes, send one.
                if (config.isEmailAlertsEnabled() && !recentlySentAppAlertsRepo.existsById(apiCall.getAppId())) {
                    String message = "An alert has been triggered for your application: " + app.getName();
                    System.out.println("Sending email.");
                    emailService.sendSimpleMessage(user.getEmail(), "API Alert | Vax Monitor", message);
                    System.out.println("Email Sent.");

                    // Add to recently sent alerts
                    recentlySentAppAlertsRepo.save(new RecentlySentAppAlerts(app.getId()));
                }
            }
        }
    }

}



