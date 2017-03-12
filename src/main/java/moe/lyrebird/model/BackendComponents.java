package moe.lyrebird.model;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import moe.lyrebird.model.threading.BackgroundService;
import moe.lyrebird.model.twitter4j.Twitter4JComponents;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.system.CleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import twitter4j.Twitter;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 *
 * For Twitter4J wrapping see {@link Twitter4JComponents}
 */
@Configuration
public class BackendComponents {
    
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterHandler twitterHandler(final ApplicationContext context, final Twitter twitter) {
        return new TwitterHandler(context, twitter);
    }
    
    @Bean
    public SessionManager sessionManager(
            final ApplicationContext context,
            final SessionRepository sessionRepository,
            final CleanupService cleanupService
    ) {
        final SessionManager manager = new SessionManager(context, sessionRepository);
        cleanupService.registerCleanupTask(manager::saveAllSessions);
        return manager;
    }
    
    @Bean
    @Autowired
    public BackgroundService backgroundService(final CleanupService cleanupService) {
        final BackgroundService backgroundService = new BackgroundService();
        cleanupService.registerCleanupTask(backgroundService::cleanUp);
        return backgroundService;
    }
}
