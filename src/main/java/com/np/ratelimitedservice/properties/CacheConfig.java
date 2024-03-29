package com.np.ratelimitedservice.properties;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@AllArgsConstructor
@ConfigurationProperties(prefix = "cache")
@ConfigurationPropertiesScan
public class CacheConfig {

    public final long size;
    public final long expirationTimeInMinutes;
}
