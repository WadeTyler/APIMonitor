package net.tylerwade.backend.services;

import net.tylerwade.backend.dto.alert.AddAlertFieldsRequest;
import net.tylerwade.backend.entity.*;
import net.tylerwade.backend.entity.alert.AlertConfig;
import net.tylerwade.backend.entity.alert.AlertField;
import net.tylerwade.backend.repository.AlertConfigRepository;
import net.tylerwade.backend.repository.ApplicationRepository;
import net.tylerwade.backend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final AlertConfigRepository alertConfigRepo;

    public AlertService(EmailService emailService, UserRepository userRepository, ApplicationRepository applicationRepository, AlertConfigRepository alertConfigRepo) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.alertConfigRepo = alertConfigRepo;
    }

    public AlertConfig getOrCreateAlertConfig(String appId) {
        Optional<AlertConfig> alertConfigOptional = alertConfigRepo.findByAppId(appId);
        if (alertConfigOptional.isEmpty()) {
            // Create a new Config
            AlertConfig config = new AlertConfig(appId);
            alertConfigRepo.save(config);
            return config;
        } else {
            return alertConfigOptional.get();
        }
    }

    public void sendAlerts(APICall apiCall) {
        // Find User
        Optional<User> userOptional = userRepository.findByAppId(apiCall.getAppId());
        if (userOptional.isEmpty()) return;
        User user = userOptional.get();

        // Find Alert Config
        Optional<AlertConfig> alertConfigOptional = alertConfigRepo.findByAppId(apiCall.getAppId());
        if (alertConfigOptional.isEmpty()) return;
        AlertConfig config = alertConfigOptional.get();

        Optional<Application> appOptional = applicationRepository.findById(apiCall.getAppId());
        if (appOptional.isEmpty()) return;
        Application app = appOptional.get();

        // Check all fields
        for (AlertField alertField : config.getAlertFields()) {
            if (checkFieldMatch(alertField, apiCall)) {
                String message = "A configured alert field has been triggered for '" + app.getName() + "'.\n\nCONFIGURED FIELD: " + alertField + "\n\nAPI CALL: " + apiCall;
                System.out.println("Sending email.");
                emailService.sendSimpleMessage(user.getEmail(), "API Alert | Vax Monitor", message);
                System.out.println("Email Sent.");
            }
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

    public AlertConfig addFieldToAlertConfig(AddAlertFieldsRequest request, String appId) throws BadRequestException {
        // Check for at least one field
        if (request.getPath() == null && request.getMethod() == null && request.getRemoteAddress() == null && request.getResponseStatus() == null) {
            throw new BadRequestException("At least one field required: Path, Method, Remote Address, Response Status");
        }

        AlertConfig config = getOrCreateAlertConfig(appId);
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

        return config;
    }

    public AlertConfig removeFieldFromAlertConfig(Long alertFieldsId, String appId) throws BadRequestException {
        // Check if null
        if (alertFieldsId == null) throw new BadRequestException("Alert Fields ID is required.");


        AlertConfig config = getOrCreateAlertConfig(appId);

        // Filter out ID
        config.getAlertFields().removeIf(field -> field.getId().equals(alertFieldsId));

        // Update and Save
        alertConfigRepo.save(config);
        return config;
    }

}






