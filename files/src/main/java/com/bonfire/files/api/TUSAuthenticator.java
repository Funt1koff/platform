package com.bonfire.files.api;

import com.bonfire.internal.api.models.Initiator;
import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@ApplicationScoped
public class TUSAuthenticator {
    private static final Logger log = Logger.getLogger(TUSAuthenticator.class);
    private static final String AUTHORIZATION = "Authorization";
    private final TUSHookException.Unauthorized unauthorized = new TUSHookException.Unauthorized("Unauthorized");

    @Inject
    private DefaultJWTParser defaultJWTParser;

    public <Out> Out authorized(Map<String, String> headers, Function<Initiator, Out> fn) {
        return authenticate(headers)
                .map(fn)
                .orElseThrow(() -> unauthorized);
    }

    private Optional<Initiator> authenticate(Map<String, String> headers) {
        String authBearer = headers.get(AUTHORIZATION);
        String auth = authBearer.substring(7);

        try {
            JsonWebToken token = defaultJWTParser.parse(auth);
            return Optional.of(extract(token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Initiator extract(JsonWebToken jsonWebToken) {
        val user = Initiator.User.newBuilder();
        val builder = Initiator.newBuilder();
        String userIdClaim = jsonWebToken.getClaim("user_id");

        if (StringUtils.isEmpty(userIdClaim)) {
            log.warn(STR."invalid user_id claim `\{userIdClaim}`");
            throw unauthorized;
        }
        try {
            val userId = Long.parseLong(userIdClaim);
            user.setUserId(userId);
        } catch (NumberFormatException e) {
            log.error(STR."invalid user_id claim `\{userIdClaim}`", e);
            throw unauthorized;
        }

        String sessionIdClaim = jsonWebToken.getClaim("sid");
        if (StringUtils.isEmpty(sessionIdClaim)) {
            log.error(STR."invalid sid claim `\{sessionIdClaim}`");
            throw unauthorized;
        }

        user.setSessionId(sessionIdClaim);
        return builder.setUser(user).build();
    }
}
