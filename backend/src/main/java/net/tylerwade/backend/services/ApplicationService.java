package net.tylerwade.backend.services;

import net.tylerwade.backend.dto.ApplicationDTO;
import net.tylerwade.backend.dto.CreateApplicationRequest;
import net.tylerwade.backend.dto.MethodCount;
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

    private int MAX_APPLICATIONS_FREE_TIER = 5;

    public ApplicationService(ApplicationRepository applicationRepository, APICallRepository apiCallRepository) {
        this.applicationRepository = applicationRepository;
        this.apiCallRepository = apiCallRepository;
    }

    public Application createApplication(CreateApplicationRequest createApplicationRequest, String userId) throws BadRequestException, NotAcceptableException {
        // Check for missing fields
        if (createApplicationRequest.getName() == null || createApplicationRequest.getName().isEmpty()) {
            throw new BadRequestException("All fields required. (Name)");
        }

        // Check if user already has max applications
        if (applicationRepository.countByUserId(userId) >= MAX_APPLICATIONS_FREE_TIER) {
            throw new NotAcceptableException("You have reached the max applications for your tier: " + MAX_APPLICATIONS_FREE_TIER + " Applications.");
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

}









