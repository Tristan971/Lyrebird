package moe.lyrebird.model;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.sessions.SessionRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 */
@Configuration
public class BackendComponents {
    
    /**
     * The consumer key secret needs to be obfuscated.
     * The authentication token and secret will be received from an oauth request. See:
     * https://dev.twitter.com/web/sign-in/implementing
     * For testing it's being loaded from a text file: resources/config.txt
     * the format is key=value with no spaces
     *
     * @return a {@link Configuration}
     */
    @Bean
    public twitter4j.conf.Configuration configuration(final Environment environment) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
    
        final String consumerKey = environment.getProperty("twitter.consumerKey");
        final String consumerSecret = environment.getProperty("twitter.consumerSecret");
        final String accessToken = "";
        final String accessTokenSecret = "";
    
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setOAuthAccessToken(accessToken);
    
        return cb.build();
    }
    
    @Bean
    public TwitterFactory twitterFactory(final twitter4j.conf.Configuration configuration) {
        return new TwitterFactory(configuration);
    }
    
    @Bean
    public Twitter twitter(final TwitterFactory factory) {
        return factory.getInstance();
    }
    
    @Bean
    public TwitterHandler twitterHandler(final Twitter twitter) {
        return new TwitterHandler(twitter);
    }
    
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public SessionManager sessionManager(final ApplicationContext context, final SessionRepository sessionRepository) {
        return new SessionManager(context, sessionRepository);
    }
}
