package com.np.ratelimitedservice.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {

    private final long retryAfterInSeconds;


    public RateLimitException(String message, long retryAfterInSeconds) {
        super(message);
        this.retryAfterInSeconds = retryAfterInSeconds;
    }
}
