package moe.lyrebird.model.twitter.services;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.function.Consumer;

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

    public void unlike(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.destroyFavorite(tweet.getId()))
                      .onSuccess(t -> LOG.debug("User {} unliked tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not unlike tweet!", err.getMessage(), err));
    }

    public void retweet(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.retweetStatus(tweet.getId()))
                      .onSuccess(t -> LOG.debug("User {} retweeted tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not retweet tweet!", err.getMessage(), err));
    }

    public void unretweet(final Status tweet) {
        final long retweetId = tweet.getCurrentUserRetweetId();
        sessionManager.doWithCurrentTwitter(twitter -> twitter.destroyStatus(retweetId))
                      .onSuccess(t -> LOG.debug(
                              "User {} unretweeted tweet {} [retweetId : {}]",
                              getCurrentScreenName(),
                              t.getId(),
                              retweetId
                      ))
                      .onFailure(err -> displayExceptionPane("Could not unretweet tweet!", err.getMessage(), err));

    }

    public void interractBinaryAction(
            final Status target,
            final Consumer<Status> doOnTrue,
            final Consumer<Status> doOnFalse,
            final boolean param
    ) {
        if (param) {
            doOnTrue.accept(target);
        } else {
            doOnFalse.accept(target);
        }
    }

    private String getCurrentScreenName() {
        return sessionManager.getCurrentTwitter()
                             .mapTry(Twitter::getScreenName)
                             .getOrElseThrow(err -> new IllegalStateException("Current user unavailable!", err));
    }

}
