package com.np.ratelimitedservice.interceptor;

import com.np.ratelimitedservice.auth.UserContext;
import com.np.ratelimitedservice.exception.ApiException;
import com.np.ratelimitedservice.exception.RateLimitException;
import com.np.ratelimitedservice.ratelimiter.plain.TokenBucketResolver;
import com.np.ratelimitedservice.ratelimiter.plain.TokenBucket;
import com.np.ratelimitedservice.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {


    private final TokenBucketResolver tokenBucketResolver;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        UserContext userContext = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TokenBucket tokenBucket = tokenBucketResolver.resolveBucket(userContext);

        try {
            tokenBucket.consume();
            response.addHeader(Constants.RATE_LIMIT_REMAINING_HEADER, String.valueOf(tokenBucket.getRemainingTokens()));
            return true;
        } catch (Exception e) {
            if (e instanceof RateLimitException) {
                response.addHeader(
                        Constants.RATE_LIMIT_RETRY_AFTER_IN_SECONDS_HEADER,
                        String.valueOf(((RateLimitException) e).getRetryAfterInSeconds())
                );
                HttpUtil.setErrorResponse(
                        response,
                        ApiException.TOO_MANY_REQUESTS.getMessage(),
                        HttpStatus.TOO_MANY_REQUESTS
                );
            }

            HttpUtil.setErrorResponse(
                    response,
                    ApiException.INTERNAL_SERVER_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            return false;
        }
    }
}
