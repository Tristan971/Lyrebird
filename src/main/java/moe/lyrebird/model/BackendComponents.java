package moe.lyrebird.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import moe.lyrebird.model.twitter4j.Twitter4JComponents;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import twitter4j.Twitter;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 * <p>
 * For Twitter4J wrapping see {@link Twitter4JComponents}
 */
@Configuration
public class BackendComponents {

    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterHandler twitterHandler(final SessionManager sessionManager, final Twitter twitter) {
        return new TwitterHandler(sessionManager, twitter);
    }

    @Bean
    public SessionManager sessionManager(
            final ApplicationContext context,
            final SessionRepository sessionRepository
    ) {
        return new SessionManager(context, sessionRepository);
    }
}
