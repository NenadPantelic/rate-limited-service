package com.np.ratelimitedservice.ratelimiter.timestamp;

import org.springframework.stereotype.Component;

@Component
public class TimestampProviderImpl implements TimestampProvider {

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
