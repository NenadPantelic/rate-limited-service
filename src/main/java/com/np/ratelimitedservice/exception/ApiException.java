package com.np.ratelimitedservice.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ApiException extends RuntimeException {

    private final String message;
    private final int status;
    private final List<String> errors;

    public static ApiException of(final String message, final int status) {
        return ApiException.builder()
                .message(message)
                .status(status)
                .errors(new ArrayList<>())
                .build();
    }

    public ApiException withMessage(final String message) {
        return ApiException.builder()
                .message(message)
                .status(this.getStatus())
                .errors(this.getErrors())
                .build();
    }

    public ApiException withStatus(final int status) {
        return ApiException.builder()
                .message(this.message)
                .status(status)
                .errors(this.getErrors())
                .build();
    }


    public ApiException withError(final String error) {
        return ApiException.builder()
                .message(this.getMessage())
                .status(this.getStatus())
                .errors(List.of(error))
                .build();
    }

    public ApiException withErrors(final List<String> errors) {
        return ApiException.builder()
                .message(this.getMessage())
                .status(this.getStatus())
                .errors(errors)
                .build();
    }

    // 400
    public static ApiException BAD_REQUEST = ApiException.of("Bad request.", 400);

    // 401
    public static ApiException UNAUTHORIZED = ApiException.of("Unauthorized.", 401);

    // 403
    public static ApiException FORBIDDEN = ApiException.of("Forbidden.", 403);

    // 404
    public static ApiException NOT_FOUND = ApiException.of("Not found.", 404);

    // 429
    public static ApiException TOO_MANY_REQUESTS = ApiException.of("Too many requests.", 429);

    // 500
    public static ApiException INTERNAL_SERVER_ERROR = ApiException.of("Internal server error.", 500);
}
