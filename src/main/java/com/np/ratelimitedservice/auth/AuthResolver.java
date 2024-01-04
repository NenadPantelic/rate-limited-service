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
     * Resolves the authentication by checking if the authorization token sent by the client is registered in the
     * repository.
     *
     * @param token the authentication token
     * @return user context attached to the given token
     * @throws ApiException (Unauthorized) if the token is invalid.
     */
    public UserContext resolve(String token) {
        log.info("Resolving the authentication....");

        UserContext userContext = USER_CONTEXT_REPOSITORY.get(token);
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

                while ((line = br.readLine()) != null) {
                    // userId, username, token
                    String[] segments = line.split(",");
                    userContextMap.put(segments[2], new UserContext(segments[0], segments[1]));
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
