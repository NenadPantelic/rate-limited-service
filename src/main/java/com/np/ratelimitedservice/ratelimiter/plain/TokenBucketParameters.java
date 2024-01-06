package com.np.ratelimitedservice.ratelimiter.plain;

public record TokenBucketParameters(int capacity, long refillInterval) {
}
