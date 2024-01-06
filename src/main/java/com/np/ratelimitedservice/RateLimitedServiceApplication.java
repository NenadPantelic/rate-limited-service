package com.np.ratelimitedservice;

import com.np.ratelimitedservice.properties.CacheConfig;
import com.np.ratelimitedservice.properties.QuoteGenerationConfig;
import com.np.ratelimitedservice.properties.RateLimitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableConfigurationProperties(value = {
        CacheConfig.class,
        QuoteGenerationConfig.class,
        RateLimitConfig.class
})
@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@ComponentScan(basePackages = {"com.np.ratelimitedservice"})
public class RateLimitedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimitedServiceApplication.class, args);
    }

}
