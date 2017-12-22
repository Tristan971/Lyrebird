package moe.lyrebird.model.tweets;

import io.vavr.control.Try;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.LinkedHashSet;

@Slf4j
@Component
public class TimelineManager {

    private final SessionManager sessionManager;

    private final ObservableSet<Status> loadedTweets;

    @Autowired
    public TimelineManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.loadedTweets = FXCollections.observableSet(new LinkedHashSet<>());
    }

    public void refreshTweets() {
        final TwitterHandler currentSessionTwitter = sessionManager.getCurrentSession().getValue();
        log.debug("Loading timeline from twitter for user : {}", currentSessionTwitter.getUserScreenName());
        Try.of(currentSessionTwitter::getTwitter)
                .mapTry(Twitter::getHomeTimeline)
                .onSuccess(loadedTweets::addAll)
                .onFailure(err -> log.error("Could not refresh timeline !", err));
        log.info("Finished refreshing tweets !");
    }

    private SetChangeListener<Status> newTweetsLoggerListener() {
        return change -> log.info("Loaded {} new tweets !", change.getSet().size());
    }

    public void subscribe(final SetChangeListener<Status> tweetChangeListener) {
        loadedTweets.addListener(tweetChangeListener);
    }
}
