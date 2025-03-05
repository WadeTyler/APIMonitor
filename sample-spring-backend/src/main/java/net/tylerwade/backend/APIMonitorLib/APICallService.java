package net.tylerwade.backend.APIMonitorLib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class APICallService {

    private static final Logger log = LoggerFactory.getLogger(APICallService.class);
    private final String URI;

    private LinkedBlockingQueue<APICall> failedReports;

    public APICallService(@Value("${apimonitor.apicalls.uri}") String uri, @Value("${apimonitor.max-failed-reports}") Integer maxFailedReports) {

        // If there's no URI throw exception
        if (uri == null) {
            throw new MonitorException("Missing required property: apimonitor.apicalls.uri");
        }
        if (maxFailedReports == null) {
            throw new MonitorException("Missing required property: apimonitor.max-failed-reports");
        }

        URI = uri;
        failedReports = new LinkedBlockingQueue<>(maxFailedReports);
    }

    public void reportAPICall(APICall apiCall) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<APICall> request = new HttpEntity<>(apiCall, headers);

        ResponseEntity<String> response;
        try {
            // Send Report
            response = restTemplate.exchange(URI, HttpMethod.POST, request, String.class);

            // If report failed to send (Bad Request/Not Found or Server Error)
            if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                throw new MonitorException("Bad Request. Please submit a bug report to API Monitor.");
            }

            // If report was successful, then attempt to send remaining failed reports
            else {
                submitFailedReports();
            }
        } catch (Exception e) {
            if (failedReports.offer(apiCall)) {
                log.warn("Failed to send API call. The API Call will attempt to send on next successful API Call: {}", e.getMessage());
            } else {
                log.warn("Failed to send API call. Failed Reports Queue is at max size and this apicall has been dropped. : {}", e.getMessage());
            }
            log.info("Failed Reports waiting to send: {}", failedReports.size());
        }
    }

    private void submitFailedReports() {
        // If we have a failed report, attempt to send it
        if (failedReports.size() > 0) {
            APICall apiCall = failedReports.poll();
            reportAPICall(apiCall);
        }
    }

    public void purgeFailedReports() {
        failedReports.clear();
    }

    public int getFailedReportCount() {
        return failedReports.size();
    }

}
