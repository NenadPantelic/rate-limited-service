package com.np.ratelimitedservice.ratelimiter.plain;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.np.ratelimitedservice.auth.UserContext;
import com.np.ratelimitedservice.properties.CacheConfig;
import com.np.ratelimitedservice.ratelimiter.timestamp.TimestampProvider;
import com.np.ratelimitedservice.service.PricingPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenBucketResolver {

    private final Cache<String, TokenBucket> cache;
    private final PricingPlanService pricingPlanService;
    private final TimestampProvider timestampProvider;

    public TokenBucketResolver(CacheConfig cacheConfig,
                               PricingPlanService pricingPlanService,
                               TimestampProvider timestampProvider) {
        cache = Caffeine.newBuilder()
                .maximumSize(cacheConfig.size)
                .expireAfterWrite(cacheConfig.expirationTimeInMinutes, TimeUnit.MINUTES)
                .build();
        this.pricingPlanService = pricingPlanService;
        this.timestampProvider = timestampProvider;
    }

    public TokenBucket resolveBucket(UserContext userContext) {
        return cache.get(userContext.id(), (key) -> newBucket(userContext));
    }

    private TokenBucket newBucket(UserContext userContext) {
        TokenBucketParameters tokenBucketParameters = pricingPlanService.getTokenBucketParameters(
                userContext.pricingPlan()
        );
        return TokenBucket.builder()
                .capacity(tokenBucketParameters.capacity())
                .refillInterval(tokenBucketParameters.refillInterval())
                .timestampProvider(timestampProvider)
                .build();
    }
}
