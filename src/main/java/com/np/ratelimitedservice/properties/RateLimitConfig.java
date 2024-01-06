package com.np.ratelimitedservice.properties;

import com.np.ratelimitedservice.interceptor.RateLimitStrategy;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@AllArgsConstructor
@ConfigurationProperties(prefix = "rate-limit")
@ConfigurationPropertiesScan
public class RateLimitConfig {

    public final RateLimitStrategy strategy;
}
