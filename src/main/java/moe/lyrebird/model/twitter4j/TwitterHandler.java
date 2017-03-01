package moe.lyrebird.model.twitter4j;

import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.SneakyThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;

/**
 * This class is a decorator around the {@link Twitter}
 * class.
 */
@Slf4j
public class TwitterHandler {
    
    private final Twitter twitter;
    
    @Autowired
    public TwitterHandler(final Twitter twitter) {
        this.twitter = twitter;
    }
    
    
    public Pair<URL, RequestToken> newSession() {
        log.info("Requesting new Session!");
        final RequestToken requestToken = SneakyThrow.unchecked(this.twitter::getOAuthRequestToken);
        log.info("Got request token : {}", requestToken.toString());
        return Pair.of(
                SneakyThrow.unchecked(() -> new URL(requestToken.getAuthorizationURL())),
                requestToken
        );
    }
    
    public boolean registerAccessToken(final RequestToken requestToken, final String pinCode) {
        log.info("Registering token {} with pincode {}", requestToken.toString(), pinCode);
        
        final Pair<AccessToken, Throwable> accessToken = SneakyThrow.uncheckedWithException(() -> {
            return this.twitter.getOAuthAccessToken(requestToken, pinCode);
        });
        
        if (accessToken.getSecond() != SneakyThrow.NO_EXCEPTION) {
            log.info("Could not get access token! An error was thrown!");
            return false;
        }
        
        this.twitter.setOAuthAccessToken(accessToken.getFirst());
        log.info(
                "Successfully got access token for user @{}! {}",
                accessToken.getFirst().getScreenName(),
                accessToken.getFirst().toString()
        );
        return true;
        //storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
    }
}
