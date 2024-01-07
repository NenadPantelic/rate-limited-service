package com.np.ratelimitedservice.service;

import com.np.ratelimitedservice.auth.PricingPlan;
import com.np.ratelimitedservice.exception.ApiException;
import com.np.ratelimitedservice.properties.RateLimitConfig;
import com.np.ratelimitedservice.ratelimiter.plain.TokenBucketParameters;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PricingPlanService {

    private final Map<PricingPlan, TokenBucketParameters> tokenBucketParametersMap;
    private final Map<PricingPlan, Bandwidth> bandwidthMap;

    public PricingPlanService(RateLimitConfig rateLimitConfig) {
        tokenBucketParametersMap = createTokenBucketParametersMap(rateLimitConfig);
        bandwidthMap = createBandwidthMap(rateLimitConfig);
    }

    public TokenBucketParameters getTokenBucketParameters(PricingPlan pricingPlan) {
        log.info("Fetching the token bucket parameters of the pricing plan {}", pricingPlan);
        TokenBucketParameters tokenBucketParameters = tokenBucketParametersMap.get(pricingPlan);

        if (tokenBucketParameters == null) {
            log.error("Internal setup mistake, pricing plan {} has no token bucket parameters defined", pricingPlan);
            throw ApiException.INTERNAL_SERVER_ERROR;
        }

        return tokenBucketParameters;
    }


    public Bandwidth getBandwidth(PricingPlan pricingPlan) {
        log.info("Fetching the rate limiting bandwidth of the pricing plan {}", pricingPlan);
        Bandwidth bandwidth = bandwidthMap.get(pricingPlan);

        if (bandwidth == null) {
            log.error("Internal setup mistake, pricing plan {} has no bandwidth defined", pricingPlan);
            throw ApiException.INTERNAL_SERVER_ERROR;
        }

        return bandwidth;
    }

    private Map<PricingPlan, TokenBucketParameters> createTokenBucketParametersMap(RateLimitConfig rateLimitConfig) {
        Map<PricingPlan, TokenBucketParameters> tokenBucketParametersMap = new HashMap<>();
        for (Map.Entry<PricingPlan, RateLimitConfig.TokenBucketConfig> entry : rateLimitConfig.tokenBucketParams.entrySet()) {
            tokenBucketParametersMap.put(
                    entry.getKey(),
                    new TokenBucketParameters(entry.getValue().capacity, entry.getValue().refillIntervalInMillis)
            );
        }

        return tokenBucketParametersMap;
    }

    private Map<PricingPlan, Bandwidth> createBandwidthMap(RateLimitConfig rateLimitConfig) {
        Map<PricingPlan, Bandwidth> bandwithMap = new HashMap<>();
        for (Map.Entry<PricingPlan, RateLimitConfig.BandwidthConfig> entry : rateLimitConfig.bandwidths.entrySet()) {
            int capacity = entry.getValue().capacity;
            int refillIntervalInMinutes = entry.getValue().refillIntervalInMinutes;

            bandwithMap.put(
                    entry.getKey(),
                    Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofMinutes(refillIntervalInMinutes)))
            );
        }

        return bandwithMap;
    }

}
