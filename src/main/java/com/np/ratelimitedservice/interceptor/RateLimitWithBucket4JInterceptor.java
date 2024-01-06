package com.np.ratelimitedservice.interceptor;

import com.np.ratelimitedservice.auth.UserContext;
import com.np.ratelimitedservice.exception.ApiException;
import com.np.ratelimitedservice.ratelimiter.bucket4j.BucketResolver;
import com.np.ratelimitedservice.util.HttpUtil;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class RateLimitWithBucket4JInterceptor implements HandlerInterceptor {

    private final BucketResolver bucketResolver;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        UserContext userContext = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket tokenBucket = bucketResolver.resolveBucket(userContext);

        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader(Constants.RATE_LIMIT_REMAINING_HEADER, String.valueOf(tokenBucket.getAvailableTokens()));
            return true;
        } else {
            long retryAfterInSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader(
                    Constants.RATE_LIMIT_RETRY_AFTER_IN_SECONDS_HEADER,
                    String.valueOf(retryAfterInSeconds)
            );
            HttpUtil.setErrorResponse(
                    response,
                    ApiException.TOO_MANY_REQUESTS.getMessage(),
                    HttpStatus.TOO_MANY_REQUESTS
            );

            return false;
        }
    }
}
