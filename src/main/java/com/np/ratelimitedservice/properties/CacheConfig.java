package com.np.ratelimitedservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationProperties(prefix = "cache")
@ConfigurationPropertiesScan
public class CacheConfig {

    private final long size;
    private final long expirationTimeInMinutes;
}
