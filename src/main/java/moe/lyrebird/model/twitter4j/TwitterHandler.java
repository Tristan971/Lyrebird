package moe.lyrebird.model.twitter4j;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.SneakyThrow;
import moe.lyrebird.model.sessions.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

/**
 * This class is a decorator around the {@link Twitter}
 * class.
 */
@Data
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterHandler {
    private final ApplicationContext context;

    private final Twitter twitter;
    private AccessToken accessToken;
    
    public Pair<URL, RequestToken> newSession() {
        log.info("Requesting new Session!");
        final RequestToken requestToken = SneakyThrow.unchecked(this.twitter::getOAuthRequestToken);
        log.info("Got request token : {}", requestToken.toString());
        return Pair.of(
                SneakyThrow.unchecked(() -> new URL(requestToken.getAuthorizationURL())),
                requestToken
        );
    }
    
    public Optional<AccessToken> registerAccessToken(final RequestToken requestToken, final String pinCode) {
        log.info("Registering token {} with pincode {}", requestToken.toString(), pinCode);
        
        final Pair<AccessToken, Throwable> accessToken = SneakyThrow.uncheckedWithException(() -> {
            // Don't refactor expression lambda into statement lambda. It's too
            // long to be treated that way.
            //noinspection CodeBlock2Expr
            return this.twitter.getOAuthAccessToken(requestToken, pinCode);
        });
        
        if (accessToken.getSecond() != SneakyThrow.NO_EXCEPTION) {
            log.info("Could not get access token! An error was thrown!");
            return Optional.empty();
        }
        
        this.twitter.setOAuthAccessToken(accessToken.getFirst());
        log.info(
                "Successfully got access token for user @{}! {}",
                accessToken.getFirst().getScreenName(),
                accessToken.getFirst().toString()
        );
        this.accessToken = accessToken.getFirst();

        this.context.getBean(SessionManager.class).addTwitterHandler(this);
        return Optional.of(accessToken.getFirst());
    }
}
