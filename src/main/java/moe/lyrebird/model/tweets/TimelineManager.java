package moe.lyrebird.model.tweets;

import io.vavr.control.Try;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class TimelineManager {

    private final SessionManager sessionManager;

    private final List<Status> loadedTweets;
    private final ObservableList<Status> observableTweetList;

    @Autowired
    public TimelineManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.loadedTweets = new LinkedList<>();
        this.observableTweetList = FXCollections.observableList(this.loadedTweets);
    }

    private void refreshTweets(final Session session) throws TwitterException {
        final TwitterHandler currentSessionTwitter = sessionManager.getCurrentSession().getValue();
        log.debug("Loading timeline from twitter for user : {}", currentSessionTwitter.getTwitter().getScreenName());
        Try.of(currentSessionTwitter::getTwitter)
                .mapTry(Twitter::getHomeTimeline)
                .onSuccess(loadedTweets::addAll);
    }

    public void subscribe(final ListChangeListener<Status> tweetChangeListener) {
        observableTweetList.addListener(tweetChangeListener);
    }
}
