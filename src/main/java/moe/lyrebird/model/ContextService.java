package moe.lyrebird.model;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterUserListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;

@Component
public class ContextService {

    private static final Logger LOG = LoggerFactory.getLogger(ContextService.class);

    private final SessionManager sessionManager;
    private final TwitterUserListener twitterUserListener;

    public ContextService(
            SessionManager sessionManager,
            TwitterUserListener twitterUserListener
    ) {
        this.sessionManager = sessionManager;
        this.twitterUserListener = twitterUserListener;
    }

    public void initialize() {
        final Property<Session> currentSessionBinding = sessionManager.currentSessionProperty();
        currentSessionBinding.addListener((observable, oldValue, newValue) -> {
            LOG.info("Switching from user session {} to {}", oldValue, newValue);
            closeSession(oldValue);
            switchToSession(newValue);
        });
    }

    private void switchToSession(final Session newSession) {

    }

    private void closeSession(final Session oldSession) {

    }

}
