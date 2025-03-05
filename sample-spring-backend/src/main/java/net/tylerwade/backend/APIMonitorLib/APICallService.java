package net.tylerwade.backend.APIMonitorLib;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class APICallService {

    private final String URI = "http://localhost:8080/api/apicalls";

    public void reportAPICall(APICall apiCall) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<APICall> request = new HttpEntity<>(apiCall, headers);

        ResponseEntity<String> response = restTemplate.exchange(URI, HttpMethod.POST, request, String.class);
    }

}
