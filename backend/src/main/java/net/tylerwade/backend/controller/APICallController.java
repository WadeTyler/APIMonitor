package net.tylerwade.backend.controller;

import net.tylerwade.backend.dto.AddAPICallRequest;
import net.tylerwade.backend.entity.APICall;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.dto.APIResponse;
import net.tylerwade.backend.entity.User;
import net.tylerwade.backend.services.APICallService;
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
    private final UserService userService;

    public APICallController(APICallService apiCallService, UserService userService) {
        this.apiCallService = apiCallService;
        this.userService = userService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<?> addAPICall(@RequestBody AddAPICallRequest addAPICallRequest, @RequestHeader String appId) {
        try {
            apiCallService.addAPICall(addAPICallRequest, appId);
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
            @CookieValue("auth_token") String authToken,
            // Params
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "50", required = false) int pageSize,
            @RequestParam(defaultValue = "", required = false) String sortBy,
            @RequestParam(defaultValue = "DESC", required = false) String direction,
            Sort sort) {
        try {
            User user = userService.getUser(authToken);

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

            Page<APICall> apiCalls = apiCallService.getApplicationAPICalls(appId, user.getId(), pageable);

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
    public ResponseEntity<?> deleteAPICall(@PathVariable("id") Long id, @CookieValue("auth_token") String authToken) {
        try {
            User user = userService.getUser(authToken);
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
            @CookieValue("auth_token") String authToken
    ) {
        try {
            User user = userService.getUser(authToken);
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
