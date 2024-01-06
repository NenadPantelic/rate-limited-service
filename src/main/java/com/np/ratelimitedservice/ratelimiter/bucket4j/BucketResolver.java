package com.np.ratelimitedservice.ratelimiter.bucket4j;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.np.ratelimitedservice.auth.UserContext;
import com.np.ratelimitedservice.properties.CacheConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BucketResolver {

    private final Cache<String, Bucket> cache;

    public BucketResolver(CacheConfig cacheConfig) {
        cache = Caffeine.newBuilder()
                .maximumSize(cacheConfig.size)
                .expireAfterWrite(cacheConfig.expirationTimeInMinutes, TimeUnit.MINUTES)
                .build();
    }

    public Bucket resolveBucket(UserContext userContext) {
        return cache.get(userContext.id(), (key) -> newBucket(userContext));
    }

    private Bucket newBucket(UserContext userContext) {
        Bandwidth bandwidth = userContext.pricingPlan().getBandwidth();
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}
