package com.np.ratelimitedservice.ratelimiter.timestamp;

public interface TimestampProvider {
    /**
     * Gets the current timestamp expresses as a long number - Unix style timing.
     */
    long now();
}
