package moe.lyrebird.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import moe.lyrebird.model.twitter.twitter4j.Twitter4JComponents;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 * <p>
 * For Twitter4J wrapping see {@link Twitter4JComponents}
 */
@Configuration
public class BackendComponents {

    private static final Logger LOG = LoggerFactory.getLogger(BackendComponents.class);

    @Bean
    @Lazy
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterHandler twitterHandler(final Twitter twitter) {
        return new TwitterHandler(twitter);
    }

    @Bean
    public SessionManager sessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        final SessionManager sessionManager = new SessionManager(context, sessionRepository);
        long loadedSessions = sessionManager.loadAllSessions();
        LOG.info("Loaded {} previously saved sessions.", loadedSessions);
        return sessionManager;
    }

}
