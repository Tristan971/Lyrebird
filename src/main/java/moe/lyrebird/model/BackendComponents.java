package moe.lyrebird.model;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import moe.lyrebird.model.twitter4j.Twitter4JComponents;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 *
 * For Twitter4J wrapping see {@link Twitter4JComponents}
 */
@Configuration
public class BackendComponents {
    
    @Bean
    public TwitterHandler twitterHandler(final ApplicationContext context, final Twitter twitter) {
        return new TwitterHandler(context, twitter);
    }
    
    @Bean
    public SessionManager sessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        return new SessionManager(context, sessionRepository);
    }
}
