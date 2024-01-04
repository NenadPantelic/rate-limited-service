package com.np.ratelimitedservice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.ratelimitedservice.exception.ApiError;
import com.np.ratelimitedservice.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final List<String> AUTH_WHITELIST = List.of(
            "/swagger-resources",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs",
            "/swagger-ui",
            "/favicon.ico"
    );

    private static final String TOKEN_HEADER = "x-auth-token";
    private static final String FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AuthResolver authResolver;

    public AuthFilter(AuthResolver authResolver) {
        this.authResolver = authResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String token = request.getHeader(TOKEN_HEADER);
        final String path = request.getRequestURI();
        final String ip = getClientIp(request);
        log.info("Request made by {} targeting {}", ip, path);

        try {
            Authentication authentication = getAuthentication(token);
            log.info("Authenticating user {}....", authentication.getPrincipal());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ApiException e) {
            log.warn("Could not authenticate the user.", e);
            setErrorResponse(
                    response,
                    e.getMessage(),
                    e.equals(ApiException.UNAUTHORIZED) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (RuntimeException e) {
            log.warn("Could not authenticate the user.", e);
            setErrorResponse(
                    response,
                    ApiException.INTERNAL_SERVER_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // http://localhost:8083/v3/api-docs
    // http://localhost:8083/swagger-ui/index.html
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        for (String whitelistedPath : AUTH_WHITELIST) {
            if (path.startsWith(whitelistedPath)) {
                return true;
            }
        }

        return false;
    }

    private Authentication getAuthentication(String token) {
        UserContext userContext = authResolver.resolve(token);
        return new UsernamePasswordAuthenticationToken(userContext.username(), null, List.of());
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddress = request.getHeader(FORWARDED_FOR_HEADER);
        if (remoteAddress == null || remoteAddress.isEmpty()) {
            remoteAddress = request.getRemoteAddr();
        }

        return remoteAddress;
    }

    private void setErrorResponse(HttpServletResponse response,
                                  String message,
                                  HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        final ApiError apiError = new ApiError(message, status.value());

        try {
            String jsonResponse = OBJECT_MAPPER.writeValueAsString(apiError);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            log.error("Unexpected error occurred", e);
            throw e;
        }
    }
}