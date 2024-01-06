package com.np.ratelimitedservice.auth;

import com.np.ratelimitedservice.exception.ApiException;
import com.np.ratelimitedservice.util.HttpUtil;
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

    private static final String API_KEY_HEADER = "x-Api-Key";
    private static final String FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    private final AuthResolver authResolver;

    public AuthFilter(AuthResolver authResolver) {
        this.authResolver = authResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isEmpty()) {
            HttpUtil.setErrorResponse(
                    response,
                    ApiException.UNAUTHORIZED.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        final String path = request.getRequestURI();
        final String ip = getClientIp(request);
        log.info("Request made by {} targeting {}", ip, path);

        try {
            Authentication authentication = getAuthentication(apiKey);
            log.info("Authenticating user {}....", authentication.getPrincipal());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ApiException e) {
            log.warn("Could not authenticate the user.", e);
            HttpUtil.setErrorResponse(
                    response,
                    e.getMessage(),
                    ApiException.UNAUTHORIZED.equals(e) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (RuntimeException e) {
            log.error("Could not authenticate the user.", e);
            HttpUtil.setErrorResponse(
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

    private Authentication getAuthentication(String apiKey) {
        UserContext userContext = authResolver.resolve(apiKey);
        return new UsernamePasswordAuthenticationToken(userContext, null, List.of());
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddress = request.getHeader(FORWARDED_FOR_HEADER);
        if (remoteAddress == null || remoteAddress.isEmpty()) {
            remoteAddress = request.getRemoteAddr();
        }

        return remoteAddress;
    }
}