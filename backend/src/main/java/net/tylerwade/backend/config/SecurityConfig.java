package net.tylerwade.backend.config;

import net.tylerwade.backend.config.filter.JwtTokenVerifier;
import net.tylerwade.backend.config.filter.JwtUsernameAndPasswordAuthenticationFilter;
import net.tylerwade.backend.config.properties.JwtConfig;
import net.tylerwade.backend.services.UserService;
import net.tylerwade.backend.services.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public SecurityConfig(UserService userService, JwtUtil jwtUtil, JwtConfig jwtConfig, AuthenticationManager authenticationManager) {
        this.jwtConfig = jwtConfig;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(Customizer.withDefaults())
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(userService, authenticationManager, jwtUtil))
                .addFilterAfter(new JwtTokenVerifier(jwtUtil, jwtConfig, userService), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/user/signup/verify", "/api/user/signup", "/api/apicalls").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
