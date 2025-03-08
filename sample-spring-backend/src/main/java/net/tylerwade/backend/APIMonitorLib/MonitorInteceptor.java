package net.tylerwade.backend.APIMonitorLib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;


@Component
public class MonitorInteceptor implements HandlerInterceptor {

    private APICallService apiCallService;


    public MonitorInteceptor(APICallService apiCallService) {
        this.apiCallService = apiCallService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws MonitorException {
        try {
            AddAPICallRequest apiCallRequest = new AddAPICallRequest();
            apiCallRequest.setPath(request.getServletPath());
            apiCallRequest.setMethod(request.getMethod());
            apiCallRequest.setResponseStatus(response.getStatus());
            apiCallRequest.setRemoteAddress(request.getRemoteAddr());
            apiCallRequest.setTimestamp(new Date(System.currentTimeMillis()).toString());

            // Report Call
            apiCallService.reportAPICall(apiCallRequest);
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

}
