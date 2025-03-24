package net.tylerwade.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.tylerwade.backend.dto.AddAPICallRequest;
import net.tylerwade.backend.entity.APICall;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.services.APICallService;
import net.tylerwade.backend.services.AlertService;
import net.tylerwade.backend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apicalls")
public class APICallController {

    private final APICallService apiCallService;
    private final AlertService alertService;

    public APICallController(APICallService apiCallService, AlertService alertService) {
        this.apiCallService = apiCallService;
        this.alertService = alertService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<?> addAPICall(@RequestBody AddAPICallRequest addAPICallRequest, @RequestHeader String appId) {
        try {
            APICall apiCall = apiCallService.addAPICall(addAPICallRequest, appId);

            // Check Alerts
            alertService.sendAlerts(apiCall);

            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(null, "API Call Added Successfully."));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getApplicationAPICalls(
            @RequestHeader("appId") String appId,
            HttpServletRequest request,
            // Params
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "50", required = false) int pageSize,
            @RequestParam(defaultValue = "timestamp", required = false) String sortBy,
            @RequestParam(defaultValue = "DESC", required = false) String direction,
            @RequestParam(required = false) String search
            ) {
        try {
            User user = (User) request.getAttribute("user");

            // Construct pageable
            PageRequest pageRequest;
            if (sortBy.isEmpty()) {
                pageRequest = PageRequest.of(pageNumber, pageSize);
            } else if (!sortBy.isEmpty() && direction.equalsIgnoreCase("DESC")) {
                pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            } else {
                pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
            }

            Pageable pageable = pageRequest;

            Page<APICall> apiCalls = apiCallService.getApplicationAPICalls(appId, user.getId(), pageable, search);

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(apiCalls, "API Calls Retrieved."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAPICall(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("user");
            apiCallService.deleteAPICall(id, user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "API Call Deleted."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping({"/", ""})
    public ResponseEntity<?> deleteAllApiCallsInApplication(
            @RequestHeader String appId,
            HttpServletRequest request
    ) {
        try {
            User user = (User) request.getAttribute("user");
            apiCallService.deleteAllAPICallsInApplication(appId, user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(null, "All API Calls in Application deleted."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error(e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error(e.getMessage()));
        }
    }

}
