package com.np.ratelimitedservice.interceptor;

import com.np.ratelimitedservice.properties.RateLimitConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@AllArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final RateLimitConfig rateLimitConfig;
    private final RateLimitInterceptor rateLimitInterceptor;
    private final RateLimitWithBucket4JInterceptor rateLimitWithBucket4jInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimitConfig.strategy == RateLimitStrategy.PLAIN) {
            log.info("Rate limit interceptor of type RateLimitInterceptor will be used.");
            registry.addInterceptor(rateLimitInterceptor)
                    .addPathPatterns("/api/v1/quotes**");
        } else if (rateLimitConfig.strategy == RateLimitStrategy.BUCKET4J) {
            log.info("Rate limit interceptor of type RateLimitWithBucket4JInterceptor will be used.");
            registry.addInterceptor(rateLimitWithBucket4jInterceptor)
                    .addPathPatterns("/api/v1/quotes**");
        } else if (rateLimitConfig.strategy == RateLimitStrategy.NONE) {
            log.info("No rate limit interceptor will be used.");
        } else {
            throw new RuntimeException("Misconfigured rate limit interceptor...");
        }
    }
}