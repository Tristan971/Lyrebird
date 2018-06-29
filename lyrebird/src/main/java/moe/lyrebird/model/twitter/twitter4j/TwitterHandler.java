package moe.lyrebird.model.twitter.twitter4j;

import org.springframework.beans.factory.annotation.Autowired;
import io.vavr.CheckedFunction0;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

import static io.vavr.API.unchecked;

/**
 * This class is a decorator around the {@link Twitter} class.
 */
public class TwitterHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterHandler.class);

    public static final AccessToken FAKE_ACCESS_TOKEN = new AccessToken("fake", "token");

    private final Twitter twitter;

    private AccessToken accessToken = FAKE_ACCESS_TOKEN;

    @Autowired
    public TwitterHandler(final Twitter twitter) {
        this.twitter = twitter;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public Tuple2<URL, RequestToken> newSession() {
        LOG.info("Requesting new Session!");
        final RequestToken requestToken = unchecked((CheckedFunction0<RequestToken>) this.twitter::getOAuthRequestToken)
                .apply();
        LOG.info("Got request token : {}", requestToken);
        return Tuple.of(
                unchecked((CheckedFunction0<URL>) (() -> new URL(requestToken.getAuthorizationURL()))).apply(),
                requestToken
        );
    }

    public Optional<AccessToken> registerAccessToken(final RequestToken requestToken, final String pinCode) {
        LOG.info("Registering token {} with pincode {}", requestToken, pinCode);

        final Try<AccessToken> tryAccessToken = Try.of(() -> {
            // Don't refactor expression lambda into statement lambda. It's too
            // long to be treated that way.
            //noinspection CodeBlock2Expr
            return this.twitter.getOAuthAccessToken(requestToken, pinCode);
        });

        if (tryAccessToken.isFailure()) {
            LOG.info("Could not get access token! An error was thrown!");
            return Optional.empty();
        }


        final AccessToken successAccessToken = tryAccessToken.get();
        this.twitter.setOAuthAccessToken(successAccessToken);
        LOG.info(
                "Successfully got access token for user @{}! {}",
                successAccessToken.getScreenName(),
                successAccessToken.toString()
        );
        this.accessToken = successAccessToken;
        return Optional.of(successAccessToken);
    }

    public void registerAccessToken(final AccessToken preloadedAccessToken) {
        this.accessToken = preloadedAccessToken;
        this.twitter.setOAuthAccessToken(preloadedAccessToken);
    }

}
