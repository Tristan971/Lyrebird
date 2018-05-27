package moe.lyrebird.model.tweets;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;

@Component
public class TimelineManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(TimelineManager.class);

    private final SessionManager sessionManager;

    private final ObservableList<Status> loadedTweets = FXCollections.observableList(new LinkedList<>());

    public TimelineManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ObservableList<Status> getLoadedTweets() {
        return loadedTweets;
    }

    public void refreshTweets() {
        sessionManager.getCurrentTwitter()
                      .mapTry(Twitter::getHomeTimeline)
                      .onSuccess(this::addLoadedTweets)
                      .onFailure(err -> LOG.error("Could not refresh timeline!", err));
    }

    public void loadMoreTweets() {
        LOG.debug("Requesting more tweets.");
        final Paging requestPaging = new Paging();
        final Status oldestLoadedTweet = loadedTweets.get(loadedTweets.size() - 1);
        LOG.debug("Oldest tweets previously loaded : {}", oldestLoadedTweet);
        requestPaging.setMaxId(oldestLoadedTweet.getId());

        sessionManager.getCurrentTwitter()
                      .mapTry(twitter -> twitter.getHomeTimeline(requestPaging))
                      .onSuccess(this::addLoadedTweets);
    }

    private void addLoadedTweets(final ResponseList<Status> loadedTweets) {
        loadedTweets.stream()
                    .filter(status -> !this.loadedTweets.contains(status))
                    .forEach(this.loadedTweets::add);
        this.loadedTweets.addAll(loadedTweets);
        loadedTweets.sort(Comparator.comparingLong(Status::getId).reversed());
        LOG.debug("Loaded {} tweets successfully.", loadedTweets.size());
    }
}
