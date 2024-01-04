package com.np.ratelimitedservice;

import com.np.ratelimitedservice.properties.CacheConfig;
import com.np.ratelimitedservice.properties.QuoteGenerationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableConfigurationProperties(value = {
        CacheConfig.class,
        QuoteGenerationConfig.class
})
@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
public class RateLimitedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimitedServiceApplication.class, args);
    }

}
