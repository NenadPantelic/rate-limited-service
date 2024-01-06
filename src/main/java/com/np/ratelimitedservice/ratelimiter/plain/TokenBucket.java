package com.np.ratelimitedservice.ratelimiter.plain;

import com.np.ratelimitedservice.exception.RateLimitException;
import com.np.ratelimitedservice.ratelimiter.timestamp.TimestampProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenBucket {

    private int capacity;
    private int count;
    // Unix-based time in milliseconds
    private long lastRefillTime;
    private long nextRefillTime;
    private TimestampProvider timestampProvider;
    private long refillInterval;

    public static Builder builder() {
        return new Builder();
    }

    // making a thread safe implementation in case multiple threads are using the same bucket
    public synchronized void consume() {
        // first refill the bucket in case it is possible
        refill();
        // all tokens are used
        if (count <= 0) {
            long retryAfterInSeconds = (nextRefillTime - timestampProvider.now()) / 1_000;
            String errMessage = "Rate limit exceeded.";
            log.warn(errMessage);
            throw new RateLimitException(errMessage, retryAfterInSeconds);
        }

        count -= 1;
    }

    private void refill() {
        long now = timestampProvider.now();
        // should be refilled
        if (now > nextRefillTime) {
            count = capacity;
            lastRefillTime = now;
            nextRefillTime = now + refillInterval;
        }
    }

    public int getRemainingTokens() {
        return count;
    }

    static class Builder {
        private int capacity;
        private TimestampProvider timestampProvider;
        private long refillInterval;

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder timestampProvider(TimestampProvider timestampProvider) {
            this.timestampProvider = timestampProvider;
            return this;
        }

        public Builder refillInterval(long refillInterval) {
            this.refillInterval = refillInterval;
            return this;
        }

        public TokenBucket build() {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Cannot create an empty bucket...");
            }

            long now = timestampProvider.now();
            return new TokenBucket(
                    capacity, capacity, now, now + refillInterval, timestampProvider, refillInterval
            );
        }

    }
}
