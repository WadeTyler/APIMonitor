package net.tylerwade.backend.services;

import net.tylerwade.backend.dto.CreateApplicationRequest;
import net.tylerwade.backend.entity.Application;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.ApplicationRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application createApplication(CreateApplicationRequest createApplicationRequest, String userId) throws BadRequestException, NotAcceptableException {
        // Check for missing fields
        if (createApplicationRequest.getName() == null || createApplicationRequest.getName().isEmpty()) {
            throw new BadRequestException("All fields required. (Name)");
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









