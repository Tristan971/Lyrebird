package moe.lyrebird.model.twitter.observables;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.api.TimelinesResources;

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
        LOG.debug("Initializing timeline manager.");
        this.sessionManager = sessionManager;
    }

    public ObservableList<Status> loadedTweets() {
        return FXCollections.unmodifiableObservableList(loadedTweets);
    }

    public void loadMoreTweets(final long loadUntilThisStatus) {
        LOG.debug("Requesting more tweets.");
        final Paging requestPaging = new Paging();
        requestPaging.setMaxId(loadUntilThisStatus);

        sessionManager.getCurrentTwitter()
                      .mapTry(twitter -> twitter.getHomeTimeline(requestPaging))
                      .onSuccess(this::addTweets);
        LOG.debug("Finished loading more tweets.");
    }

    public void loadLastTweets() {
        LOG.debug("Requesting last tweets in timeline.");
        sessionManager.getCurrentTwitter()
                      .mapTry(TimelinesResources::getHomeTimeline)
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
