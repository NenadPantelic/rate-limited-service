package com.np.ratelimitedservice.properties;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@AllArgsConstructor
@ConfigurationProperties(prefix = "quote.generation")
@ConfigurationPropertiesScan
public class QuoteGenerationConfig {

    public final int limit;
    public final int minWordsCounter;
    public final int maxWordsCounter;
}
