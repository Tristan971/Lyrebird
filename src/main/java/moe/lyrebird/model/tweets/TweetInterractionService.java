package moe.lyrebird.model.tweets;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;

import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

@Component
public class TweetInterractionService {

    private static final Logger LOG = LoggerFactory.getLogger(TweetInterractionService.class);

    private final SessionManager sessionManager;

    public TweetInterractionService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void like(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.createFavorite(tweet.getId()))
                      .onSuccess(t -> LOG.debug("User {} liked tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not like tweet!", err.getMessage(), err));
    }

    public void retweet(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.retweetStatus(tweet.getId()))
                      .onSuccess(t -> LOG.debug("User {} retweeted tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not retweet tweet!", err.getMessage(), err));

    }

    private String getCurrentScreenName() {
        return sessionManager.getCurrentTwitter()
                             .mapTry(Twitter::getScreenName)
                             .getOrElseThrow(err -> new IllegalStateException("Current user unavailable!", err));
    }

}
