package moe.lyrebird.model.twitter4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by Tristan on 22/02/2017.
 */
@Configuration
public class Twitter4JComponents {
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterConfiguration configuration(final Environment environment) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        
        final String consumerKey = environment.getProperty("twitter.consumerKey");
        final String consumerSecret = environment.getProperty("twitter.consumerSecret");
        final String accessToken = "";
        final String accessTokenSecret = "";
        
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setOAuthAccessToken(accessToken);
        
        return new TwitterConfiguration(cb.build());
    }
    
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public TwitterFactory twitterFactory(final TwitterConfiguration configuration) {
        return new TwitterFactory(configuration.getConfiguration());
    }
    
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public Twitter twitter(final TwitterFactory factory) {
        return factory.getInstance();
    }
    
}
