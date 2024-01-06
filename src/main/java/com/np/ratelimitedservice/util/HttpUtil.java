package com.np.ratelimitedservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.ratelimitedservice.exception.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class HttpUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void setErrorResponse(HttpServletResponse response,
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
