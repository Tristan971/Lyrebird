package moe.lyrebird.model.twitter.observables;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Component
public class Timeline {

    private static final Logger LOG = LoggerFactory.getLogger(Timeline.class);

    private final SessionManager sessionManager;

    private final ObservableList<Status> loadedTweets = FXCollections.observableList(new LinkedList<>());

    public Timeline(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ObservableList<Status> loadedTweets() {
        return FXCollections.unmodifiableObservableList(loadedTweets);
    }

    public void manuallyRefreshTweets() {
        sessionManager.getCurrentTwitter()
                      .mapTry(Twitter::getHomeTimeline)
                      .onSuccess(this::addTweets)
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
                      .onSuccess(this::addTweets);
    }

    private void addTweets(final List<Status> receivedTweets) {
        receivedTweets.forEach(this::addTweet);
        LOG.debug("Loaded {} tweets successfully.", receivedTweets.size());
    }

    public void addTweet(final Status newTweet) {
        if (!this.loadedTweets.contains(newTweet)) {
            this.loadedTweets.add(newTweet);
            this.loadedTweets.sort(Comparator.comparingLong(Status::getId).reversed());
        }

    }

    public void removeTweet(final long removedId) {
        this.loadedTweets.stream()
                                    .filter(status -> status.getId() == removedId)
                                    .findFirst()
                                    .ifPresent(this.loadedTweets::remove);
    }

}
