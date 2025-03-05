package net.tylerwade.backend;

import net.tylerwade.backend.APIMonitorLib.APICallService;
import net.tylerwade.backend.APIMonitorLib.MonitorInteceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MonitorInteceptor monitorInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add MonitorInterceptor to all paths
        registry.addInterceptor(monitorInteceptor);
    }

}
