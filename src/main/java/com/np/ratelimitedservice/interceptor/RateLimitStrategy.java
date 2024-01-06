package com.np.ratelimitedservice.interceptor;


public enum RateLimitStrategy {
    PLAIN,
    BUCKET4J,
    NONE
}