package net.tylerwade.backend.services;

import net.tylerwade.backend.config.VaxProperties;
import net.tylerwade.backend.dto.ApplicationDTO;
import net.tylerwade.backend.dto.CreateApplicationRequest;
import net.tylerwade.backend.dto.MethodCount;
import net.tylerwade.backend.dto.UpdateApplicationRequest;
import net.tylerwade.backend.entity.Application;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.APICallRepository;
import net.tylerwade.backend.repository.ApplicationRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final APICallRepository apiCallRepository;
    private final VaxProperties vaxProperties;

    public ApplicationService(ApplicationRepository applicationRepository, APICallRepository apiCallRepository, VaxProperties vaxProperties) {
        this.applicationRepository = applicationRepository;
        this.apiCallRepository = apiCallRepository;
        this.vaxProperties = vaxProperties;
    }

    public Application createApplication(CreateApplicationRequest createApplicationRequest, String userId) throws BadRequestException, NotAcceptableException {
        // Check for missing fields
        if (createApplicationRequest.getName() == null || createApplicationRequest.getName().isEmpty()) {
            throw new BadRequestException("All fields required. (Name)");
        }

        // Check if user already has max applications
        if (applicationRepository.countByUserId(userId) >= vaxProperties.getMaxApplicationsFreeTier()) {
            throw new NotAcceptableException("You have reached the max applications for your tier: " + vaxProperties.getMaxApplicationsFreeTier() + " Applications.");
        }

        // Check if user already has an application with that name.
        if (applicationRepository.existsByNameAndUserIdIgnoreCase(createApplicationRequest.getName(), userId)) {
            throw new NotAcceptableException("You already have an application with that name.");
        }


        // Create new application
        Application application = new Application(userId, createApplicationRequest.getName());
        applicationRepository.save(application);
        return application;
    }

    public Application getApplicationFromPublicToken(String publicToken, String userId) throws UnauthorizedException, NotFoundException, BadRequestException {
        // Check for missing field
        if (publicToken == null || publicToken.isEmpty()) {
            throw new BadRequestException("All fields required. (Public Token)");
        }

        // Find application
        Optional<Application> applicationOptional = applicationRepository.findByPublicToken(publicToken);
        if (applicationOptional.isEmpty()) {
            throw new NotFoundException("Application not found.");
        }

        Application application = applicationOptional.get();

        // Check if owner
        if (!application.getUserId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized.");
        }

        // Return application
        return application;
    }

    public Application getApplication(String appId) throws NotFoundException {
        // Find application
        Optional<Application> appOptional = applicationRepository.findById(appId);
        if (appOptional.isEmpty()) throw new NotFoundException("Application not found");

        return appOptional.get();
    }

    public Application getApplicationWithAuth(String appId, String userId) throws UnauthorizedException, NotFoundException {
        Application app = getApplication(appId);

        if (!app.getUserId().equals(userId)) throw new UnauthorizedException("Unauthorized");

        return app;
    }

    public boolean applicationExistsAndIsOwner(String appId, String userId) {
        return applicationRepository.existsByIdAndUserId(appId, userId);
    }

    public void deleteApplication(String appId, String userId) throws UnauthorizedException, NotFoundException, BadRequestException {
        // Check for missing fields
        if (appId == null || appId.isEmpty()) {
            throw new BadRequestException("All fields required. (AppId)");
        }

        // Find application
        Optional<Application> applicationOptional = applicationRepository.findById(appId);
        if (applicationOptional.isEmpty()) {
            throw new NotFoundException("Application not found.");
        }

        Application application = applicationOptional.get();

        // Check if owner
        if (!application.getUserId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized.");
        }

        // Delete application
        applicationRepository.delete(application);
    }

    public List<Application> getAllApplications(String userId) {
        return applicationRepository.findAllByUserId(userId);
    }

    public Long getTotalAPICalls(String appId) {
        return apiCallRepository.countDistinctAPICallByAppId(appId);
    }

    public Long getTotalUniqueRemoteAddr(String appId) {
        return apiCallRepository.countDistinctAPICallByRemoteAddressAndAppIdEquals(appId);
    }

    public String[] getUniquePaths(String appId) {
        return apiCallRepository.findDistinctPathByAppId(appId);
    }

    public MethodCount[] getMethodCounts(String appId) {
        // Get Distinct methods.
        String[] methods = apiCallRepository.findDistinctMethodByAppId(appId);

        // Get Counts
        List<MethodCount> methodCounts = new ArrayList<>();
        for (String method : methods) {
            methodCounts.add(new MethodCount(method, apiCallRepository.countMethodByAndAppIdEquals(method, appId)));
        }

        return methodCounts.toArray(new MethodCount[methodCounts.size()]);
    }

    public ApplicationDTO convertApplicationToDTO(Application application) {
        return new ApplicationDTO(
                application.getId(),
                application.getPublicToken(),
                application.getName(),
                application.getUserId(),
                getTotalAPICalls(application.getId()),
                getTotalUniqueRemoteAddr(application.getId()),
                getUniquePaths(application.getId()),
                getMethodCounts(application.getId())
        );
    }

    public Application findApplicationById(String appId) throws NotFoundException {
        Optional<Application> applicationOptional = applicationRepository.findById(appId);
        if (applicationOptional.isEmpty()) {
            throw new NotFoundException("Application not found.");
        }

        return applicationOptional.get();
    }

    public Application findApplicationAndVerifyUser(String appId, String userId) throws UnauthorizedException, NotFoundException {
        Application application = findApplicationById(appId);

        if (!application.getUserId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized.");
        }

        return application;
    }

    public Application updateApplication(String appId, UpdateApplicationRequest updateRequest, String userId) throws BadRequestException, NotFoundException, UnauthorizedException, NotAcceptableException {

        // Check for fields.
        if (updateRequest.getName() == null || updateRequest.getName().isEmpty()) {
            throw new BadRequestException("Required field missing: (name)");
        }

        // Check if application exists.
        Optional<Application> appOptional = applicationRepository.findById(appId);
        if (appOptional.isEmpty()) {
            throw new NotFoundException("Invalid Application ID. Not Found.");
        }

        Application app = appOptional.get();

        // Check if owner.
        if (!app.getUserId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Check if user has another app with the name.
        Optional<Application> existingByName = applicationRepository.findByNameIgnoreCaseAndUserIdIgnoreCase(updateRequest.getName(), userId);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(appId)) {
            throw new NotAcceptableException("You already have an application with that name.");
        }

        // Update application name.
        app.setName(updateRequest.getName());
        applicationRepository.save(app);

        return app;
    }

}









