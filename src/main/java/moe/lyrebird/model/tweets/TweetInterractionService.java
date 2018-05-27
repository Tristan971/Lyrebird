package moe.lyrebird.model.tweets;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import twitter4j.Status;
import twitter4j.Twitter;

import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

@Slf4j
@Component
@RequiredArgsConstructor
public class TweetInterractionService {

    private final SessionManager sessionManager;

    public void like(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.createFavorite(tweet.getId()))
                      .onSuccess(t -> log.debug("User {} liked tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not like tweet!", err.getMessage(), err));
    }

    public void retweet(final Status tweet) {
        sessionManager.doWithCurrentTwitter(twitter -> twitter.retweetStatus(tweet.getId()))
                      .onSuccess(t -> log.debug("User {} retweeted tweet {}", getCurrentScreenName(), t.getId()))
                      .onFailure(err -> displayExceptionPane("Could not retweet tweet!", err.getMessage(), err));

    }

    private String getCurrentScreenName() {
        return sessionManager.getCurrentTwitter()
                             .mapTry(Twitter::getScreenName)
                             .getOrElseThrow(err -> new IllegalStateException("Current user unavailable!", err));
    }

}
