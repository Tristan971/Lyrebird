package moe.lyrebird.model.tweets;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import twitter4j.Status;
import twitter4j.Twitter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.LinkedHashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimelineManager {

    private final SessionManager sessionManager;
    private final ObservableSet<Status> loadedTweets = FXCollections.observableSet(new LinkedHashSet<>());

    public void refreshTweets() {
        sessionManager.getCurrentSession()
                      .toTry()
                      .map(Session::getTwitterHandler)
                      .map(TwitterHandler::getTwitter)
                      .andThenTry(session -> log.debug(
                              "Loading timeline from twitter for user : {}",
                              session.getScreenName()
                      ))
                      .mapTry(Twitter::getHomeTimeline)
                      .onSuccess(statuses -> {
                          this.loadedTweets.addAll(statuses);
                          log.debug("Loaded {} tweets successfully.", statuses.size());
                      })
                      .onFailure(err -> log.error("Could not refresh timeline!", err));
    }

    public void subscribe(final SetChangeListener<Status> tweetChangeListener) {
        loadedTweets.addListener(tweetChangeListener);
    }
}
