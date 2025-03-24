package net.tylerwade.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.tylerwade.backend.model.dto.APIResponse;
import net.tylerwade.backend.model.dto.ApplicationDTO;
import net.tylerwade.backend.model.dto.CreateApplicationRequest;
import net.tylerwade.backend.model.dto.UpdateApplicationRequest;
import net.tylerwade.backend.model.entity.Application;
import net.tylerwade.backend.exceptions.NotAcceptableException;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.model.entity.User;
import net.tylerwade.backend.services.ApplicationService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplication(HttpServletRequest request, @RequestBody CreateApplicationRequest createApplicationRequest) {
        try {
            User user = (User) request.getAttribute("user");
            Application application = applicationService.createApplication(createApplicationRequest, user.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(application, "Application created."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{publicToken}")
    public ResponseEntity<?> getApplicationFromPublicToken(HttpServletRequest request, @PathVariable String publicToken) {
        try {
            User user = (User) request.getAttribute("user");
            Application application = applicationService.getApplicationFromPublicToken(publicToken, user.getId());

            // Convert to DTO
            ApplicationDTO applicationDTO = applicationService.convertApplicationToDTO(application);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(applicationDTO, "Application Retrieved."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteApplication(@RequestHeader String appId, HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("user");
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
    public ResponseEntity<?> getAllApplications(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        List<Application> applications = applicationService.getAllApplications(user.getId());

        // Convert to DTO
        List<ApplicationDTO> applicationDTOS = new ArrayList<>();
        for (Application application : applications) {
            applicationDTOS.add(applicationService.convertApplicationToDTO(application));
        }

        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(applicationDTOS, "Applications retrieved successfully."));
    }

    @PutMapping({"/", ""})
    public ResponseEntity<?> updateApplication(HttpServletRequest request, @RequestHeader String appId, @RequestBody UpdateApplicationRequest updateRequest) {
        try {
            User user = (User) request.getAttribute("user");
            Application app = applicationService.updateApplication(appId, updateRequest, user.getId());

            // Convert App to DTO
            ApplicationDTO appDTO = applicationService.convertApplicationToDTO(app);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(appDTO, "Application Updated Successfully."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (NotAcceptableException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(APIResponse.error(e.getMessage()));
        }
    }

}
