package moe.lyrebird.model.twitter4j;

import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

import static io.vavr.API.unchecked;

/**
 * This class is a decorator around the {@link Twitter}
 * class.
 */
@Data
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterHandler {
    public static final AccessToken FAKE_ACCESS_TOKEN = new AccessToken("fake", "token");
    
    private final ApplicationContext context;

    private final Twitter twitter;
    private AccessToken accessToken = FAKE_ACCESS_TOKEN;
    
    public Pair<URL, RequestToken> newSession() {
        log.info("Requesting new Session!");
        final RequestToken requestToken = unchecked((CheckedFunction0<RequestToken>) this.twitter::getOAuthRequestToken).apply();
        log.info("Got request token : {}", requestToken.toString());
        return Pair.of(
                unchecked((CheckedFunction0<URL>) (() -> new URL(requestToken.getAuthorizationURL()))).apply(),
                requestToken
        );
    }
    
    public Optional<AccessToken> registerAccessToken(final RequestToken requestToken, final String pinCode) {
        log.info("Registering token {} with pincode {}", requestToken.toString(), pinCode);
    
        final Try<AccessToken> tryAccessToken = Try.of(() -> {
            // Don't refactor expression lambda into statement lambda. It's too
            // long to be treated that way.
            //noinspection CodeBlock2Expr
            return this.twitter.getOAuthAccessToken(requestToken, pinCode);
        });
    
        if (tryAccessToken.isFailure()) {
            log.info("Could not get access token! An error was thrown!");
            return Optional.empty();
        }
    
    
        final AccessToken successAccessToken = tryAccessToken.get();
        this.twitter.setOAuthAccessToken(successAccessToken);
        log.info(
                "Successfully got access token for user @{}! {}",
                successAccessToken.getScreenName(),
                successAccessToken.toString()
        );
        this.accessToken = successAccessToken;

        this.context.getBean(SessionManager.class).addTwitterHandler(this);
        return Optional.of(successAccessToken);
    }
}
