package net.tylerwade.backend.APIMonitorLib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class MonitorInteceptor implements HandlerInterceptor {

    private APICallService apiCallService;
    private String serviceId;

    public MonitorInteceptor(APICallService apiCallService, @Value("${apimonitor.serviceid}") String serviceId) {
        this.apiCallService = apiCallService;
        this.serviceId = serviceId;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws MonitorException {
        try {
            // Check for serviceId
            if (serviceId == null || serviceId.isEmpty()) {
                throw new MonitorException("Missing service id. Please add 'apimonitor.serviceid' property to the application.properties file.");
            }

            // Create the apiCall model.
            APICall apiCall = new APICall(request.getServletPath(), request.getMethod(), response.getStatus(), request.getRemoteAddr());

            // Add service id
            apiCall.setServiceId(serviceId);

            // Report Call
            apiCallService.reportAPICall(apiCall);
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

}
