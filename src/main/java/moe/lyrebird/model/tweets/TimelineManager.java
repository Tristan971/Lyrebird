package moe.lyrebird.model.tweets;

import com.google.common.collect.EvictingQueue;
import io.vavr.control.Try;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

@Component
public class TimelineManager {

    private final SessionManager sessionManager;

    private final Queue<Status> loadedTweets;

    @Autowired
    public TimelineManager(
            final SessionManager sessionManager,
            @Value("${timelineQueue.size}") final Integer cacheSize
    ) {
        this.sessionManager = sessionManager;
        this.loadedTweets = EvictingQueue.create(cacheSize);
    }

    private void refreshTweets(final Session session) {
        final TwitterHandler currentSessionTwitter = sessionManager.getCurrentSession().getValue();
        Try.of(currentSessionTwitter::getTwitter)
                .mapTry(Twitter::getHomeTimeline)
                .onSuccess(loadedTweets::addAll);
    }

    public List<Status> getTweets() {
        return Collections.unmodifiableList(new ArrayList<>(loadedTweets));
    }
}
