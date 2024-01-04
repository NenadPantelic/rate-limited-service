package com.np.ratelimitedservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationProperties(prefix = "quote.generation")
@ConfigurationPropertiesScan
public class QuoteGenerationConfig {

    private final int limit;
    private final int minWordsCounter;
    private final int maxWordsCounter;
}
