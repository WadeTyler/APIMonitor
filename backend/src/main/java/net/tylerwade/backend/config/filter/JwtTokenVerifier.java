package net.tylerwade.backend.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.backend.config.properties.JwtConfig;
import net.tylerwade.backend.model.dto.APIResponse;
import net.tylerwade.backend.exceptions.UnauthorizedException;
import net.tylerwade.backend.model.SecurityUser;
import net.tylerwade.backend.services.UserService;
import net.tylerwade.backend.services.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> excludedPaths = Arrays.asList("/api/user/signup", "/api/user/signup/verify");

    public JwtTokenVerifier(JwtUtil jwtUtil, JwtConfig jwtConfig, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        if (excludedPaths.contains(requestPath) || (requestPath.equals("/api/apicalls") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Get token from cookies
            String token = jwtUtil.getAuthTokenCookieFromRequest(request).getValue();

            // Remove "Bearer " prefix
            token = token.replace(jwtConfig.getTokenPrefix(), "");

            // Check if user logged this token out already.
            if (jwtUtil.isTokenBlacklisted(token)) {
                throw new UnauthorizedException("Token blacklisted.");
            }

            String tokenSub = jwtUtil.decodeToken(token);
            SecurityUser securityUser = userService.loadUserByUserId(tokenSub);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    securityUser.getUsername(),
                    securityUser.getPassword(),
                    null
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Add user to request values
            request.setAttribute("user", securityUser.getUser());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(APIResponse.error("Unauthorized")));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
