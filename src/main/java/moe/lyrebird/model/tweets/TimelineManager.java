package moe.lyrebird.model.tweets;

import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimelineManager {

    private final SessionManager sessionManager;

    @Getter
    private final ObservableList<Status> loadedTweets = FXCollections.observableList(new LinkedList<>());

    public void refreshTweets() {
        sessionManager.getCurrentTwitter()
                      .mapTry(Twitter::getHomeTimeline)
                      .onSuccess(this::addLoadedTweets)
                      .onFailure(err -> log.error("Could not refresh timeline!", err));
    }

    public void loadMoreTweets() {
        log.debug("Requesting more tweets.");
        final Paging requestPaging = new Paging();
        final Status oldestLoadedTweet = loadedTweets.get(loadedTweets.size() - 1);
        log.debug("Oldest tweets previously loaded : {}", oldestLoadedTweet);
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
        log.debug("Loaded {} tweets successfully.", loadedTweets.size());
    }
}
