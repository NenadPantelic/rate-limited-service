package com.np.ratelimitedservice.properties;

import com.np.ratelimitedservice.auth.PricingPlan;
import com.np.ratelimitedservice.interceptor.RateLimitStrategy;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;


@AllArgsConstructor
@ConfigurationProperties(prefix = "rate-limit")
@ConfigurationPropertiesScan
public class RateLimitConfig {

    public final RateLimitStrategy strategy;
    public final Map<PricingPlan, TokenBucketConfig> tokenBucketParams;
    public final Map<PricingPlan, BandwidthConfig> bandwidths;

    @ToString
    @Setter
    public static class TokenBucketConfig {
        public int capacity;
        public int refillIntervalInMillis;
    }

    @ToString
    @Setter
    public static class BandwidthConfig {
        public int capacity;
        public int refillIntervalInMinutes;
    }
}
