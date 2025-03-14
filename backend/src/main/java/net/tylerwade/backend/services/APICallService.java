package net.tylerwade.backend.services;

import net.tylerwade.backend.config.VaxProperties;
import net.tylerwade.backend.dto.AddAPICallRequest;
import net.tylerwade.backend.entity.APICall;
import net.tylerwade.backend.exceptions.NotFoundException;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.repository.APICallRepository;
import net.tylerwade.backend.repository.ApplicationRepository;
import net.tylerwade.backend.util.MathUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class APICallService {

    // Dependencies
    private final APICallRepository apiCallRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;
    private final VaxProperties vaxProperties;

    public APICallService(APICallRepository apiCallRepository, ApplicationRepository applicationRepository, ApplicationService applicationService, VaxProperties vaxProperties) {
        this.apiCallRepository = apiCallRepository;
        this.applicationRepository = applicationRepository;
        this.applicationService = applicationService;
        this.vaxProperties = vaxProperties;
    }

    public APICall addAPICall(AddAPICallRequest addAPICallRequest, String appId) throws NotFoundException, BadRequestException {
        // Check missing fields
        if (addAPICallRequest.getPath() == null || addAPICallRequest.getPath().isEmpty() ||
        addAPICallRequest.getMethod() == null || addAPICallRequest.getMethod().isEmpty() ||
        addAPICallRequest.getResponseStatus() == null || addAPICallRequest.getResponseStatus() <= 0 ||
        addAPICallRequest.getTimestamp() == null) {
            throw new BadRequestException("All fields required. (Path, Method, ResponseStatus, Timestamp)");
        }

        // Check application exists
        if (!applicationRepository.existsById(appId)) {
            throw new NotFoundException("Invalid Application Id. Application not found.");
        }

        // Add API Call
        APICall apiCall = new APICall(appId, addAPICallRequest.getPath(), addAPICallRequest.getMethod(), addAPICallRequest.getResponseStatus(), addAPICallRequest.getTimestamp());

        // Add remote address if present
        if (addAPICallRequest.getRemoteAddress() != null && !addAPICallRequest.getRemoteAddress().isEmpty()) {
            apiCall.setRemoteAddress(addAPICallRequest.getRemoteAddress());
        }

        // Save
        apiCallRepository.save(apiCall);

        // Check if at max calls
        if (apiCallRepository.countAPICallByAppId(appId) >= vaxProperties.getMaxApiCallsFreeTier()) {
            // Delete oldest percentage
            int deleteTop = vaxProperties.getMaxApiCallsFreeTier() / 10;
            List<Long> deleteIds = apiCallRepository.findTopIdsByIdOrderByTimestampAsc(appId, deleteTop);

            apiCallRepository.deleteByIds(deleteIds);
        }

        return apiCall;
    }

    public Page<APICall> getApplicationAPICalls(String appId, String userId, Pageable pageable, String search) throws NotFoundException, BadRequestException, UnauthorizedException {
        // Check fields
        if (appId == null || appId.isEmpty() || userId == null || userId.isEmpty() || pageable == null) {
            throw new BadRequestException("All fields required. (appId, userId, pageRequest)");
        }

        applicationService.findApplicationAndVerifyUser(appId, userId);

        if (search != null && MathUtil.isInteger(search)) {
            // Search through path/method/responseStatus
            return apiCallRepository.findAllByAppIdAndPathContainingIgnoreCaseOrMethodContainingIgnoreCaseOrResponseStatusEquals(appId, search, search, Integer.parseInt(search), pageable);

        } else if (search != null) {
            // Search through path/method
            return apiCallRepository.findAllByAppIdAndPathContainingIgnoreCaseOrMethodContainingIgnoreCase(appId, search, search, pageable);
        } else {
            // No Search
            return apiCallRepository.findAllByAppId(appId, pageable);
        }
    }

    public void deleteAPICall(Long apiCallId, String userId) throws BadRequestException, NotFoundException, UnauthorizedException {
        // Check fields
        if (apiCallId == null || apiCallId <= 0 || userId == null || userId.isEmpty()) {
            throw new BadRequestException("All fields required. (API Call ID, User Id)");
        }

        // Check if api call exists
        Optional<APICall> apiCallOptional = apiCallRepository.findById(apiCallId);
        if (apiCallOptional.isEmpty()) {
            throw new NotFoundException("API Call not found.");
        }

        APICall apiCall = apiCallOptional.get();

        // Check if owner of api call
        applicationService.findApplicationAndVerifyUser(apiCall.getAppId(), userId);

        // Delete api call
        apiCallRepository.delete(apiCall);
    }

    public void deleteAllAPICallsInApplication(String appId, String userId) throws BadRequestException, UnauthorizedException, NotFoundException {
        // Check fields
        if (appId == null || appId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new BadRequestException("All fields required. (API Call ID, User Id)");
        }

        // Check if application exists
        applicationService.findApplicationAndVerifyUser(appId, userId);

        // Delete all api calls in app
        apiCallRepository.deleteAllByAppId(appId);
    }



}
