package com.np.ratelimitedservice.auth;

import com.np.ratelimitedservice.ratelimiter.plain.TokenBucketParameters;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;

public enum PricingPlan {

    FREE { // 60 min -> 100 requests/hour

        public TokenBucketParameters getTokenBucketParameters() {
            return new TokenBucketParameters(100, 60 * 60 * 1000);
        }

        public Bandwidth getBandwidth() {
            return Bandwidth.classic(100, Refill.intervally(100, Duration.ofHours(1)));
        }
    },
    BASIC { // 30 min -> 200 requests/hour

        public TokenBucketParameters getTokenBucketParameters() {

            return new TokenBucketParameters(100, 30 * 60 * 1000);
        }

        public Bandwidth getBandwidth() {
            return Bandwidth.classic(10, Refill.intervally(200, Duration.ofHours(1)));
        }
    },
    PRO { // 20 min -> 300 requests/hour

        public TokenBucketParameters getTokenBucketParameters() {
            return new TokenBucketParameters(100, 20 * 60 * 1000);
        }

        public Bandwidth getBandwidth() {
            return Bandwidth.classic(300, Refill.intervally(300, Duration.ofHours(1)));
        }
    },
    BUSINESS { // 10 min -> 600 requests/hour

        public TokenBucketParameters getTokenBucketParameters() {
            return new TokenBucketParameters(100, 10 * 60 * 1000);
        }

        public Bandwidth getBandwidth() {
            return Bandwidth.classic(600, Refill.intervally(600, Duration.ofHours(1)));
        }
    };

    public abstract TokenBucketParameters getTokenBucketParameters();

    public abstract Bandwidth getBandwidth();
}
