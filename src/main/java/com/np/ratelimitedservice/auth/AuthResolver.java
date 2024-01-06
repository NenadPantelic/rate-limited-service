package com.np.ratelimitedservice.auth;

import com.np.ratelimitedservice.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthResolver {

    private static final Map<String, UserContext> USER_CONTEXT_REPOSITORY = loadUserContextData();

    /**
     * Resolves the authentication by checking whether the API key sent by the client is registered in the
     * repository.
     *
     * @param apiKey the API key used to authenticate the user
     * @return user context attached to the given API key
     * @throws ApiException (Unauthorized) if the API key is invalid.
     */
    public UserContext resolve(String apiKey) {
        log.info("Resolving the authentication....");

        UserContext userContext = USER_CONTEXT_REPOSITORY.get(apiKey);
        if (userContext == null) {
            throw ApiException.UNAUTHORIZED;
        }

        return userContext;
    }

    private static Map<String, UserContext> loadUserContextData() {
        log.info("Loading in the user context repository...");
        Map<String, UserContext> userContextMap = new HashMap<>();

        try {
            File file = ResourceUtils.getFile("classpath:auth.csv");
            InputStream inputStream = new FileInputStream(file);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;

                // header
                br.readLine();

                while ((line = br.readLine()) != null) {
                    // userId, username, apiKey, pricingPlan
                    String[] segments = line.split(",");
                    try {
                        PricingPlan pricingPlan = PricingPlan.valueOf(segments[3]);
                        userContextMap.put(segments[2], new UserContext(segments[0], segments[1], pricingPlan));
                    } catch (IllegalArgumentException e) {
                        log.warn("Could not ingest the user: {}", line, e);
                    }
                }

                return userContextMap;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
