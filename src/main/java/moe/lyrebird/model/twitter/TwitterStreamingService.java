package moe.lyrebird.model.twitter;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.interrupts.CleanupOperation;
import moe.lyrebird.model.interrupts.CleanupService;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterUserListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterStream;

import javafx.beans.property.Property;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TwitterStreamingService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamingService.class);

    private final TwitterStream twitterStream;
    private final SessionManager sessionManager;
    private final CleanupService cleanupService;
    private final TwitterUserListener twitterUserListener;

    private final AtomicBoolean currentlyListening;

    public TwitterStreamingService(
            final TwitterStream twitterStream,
            final SessionManager sessionManager,
            final CleanupService cleanupService,
            final TwitterUserListener twitterUserListener
    ) {
        this.cleanupService = cleanupService;
        LOG.debug("Starting twitter stream connection...");
        this.currentlyListening = new AtomicBoolean(false);
        this.twitterStream = twitterStream;
        this.sessionManager = sessionManager;
        this.twitterUserListener = twitterUserListener;
        startListening();
    }

    private void startListening() {
        LOG.debug("Preparing streaming service...");
        final Property<Session> currentSessionBinding = sessionManager.currentSessionProperty();
        currentSessionBinding.addListener((observable, oldValue, newValue) -> {
            this.currentlyListening.set(true);
            if (oldValue != null) {
                closeSession();
            }
            switchToSession(newValue);
        });

        if (!currentlyListening.get()) {
            if (currentSessionBinding.getValue() != null) {
                LOG.debug("Performing initial session initialization.");
                switchToSession(currentSessionBinding.getValue());
            } else {
                LOG.debug("Not logged in. Not starting streaming service.");
            }
        }

        cleanupService.registerCleanupOperation(new CleanupOperation(
                "Stop Twitter streaming listeners",
                this::closeSession
        ));
        cleanupService.registerCleanupOperation(new CleanupOperation(
                "Stop Twitter4J's internal dispatcher thread",
                twitterStream::shutdown
        ));
    }

    private void switchToSession(final Session newSession) {
        LOG.info("Starting streaming for session {}", newSession);
        twitterStream.setOAuthAccessToken(newSession.getAccessToken());
        twitterStream.addListener(twitterUserListener);
        twitterStream.user();
    }

    private void closeSession() {
        LOG.info("Stopping streaming for current session.");
        twitterStream.clearListeners();
    }

}
