package moe.lyrebird.model.twitter4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Tristan on 22/02/2017.
 */
@Configuration
public class Twitter4JComponents {
    @Bean
    public TwitterConfiguration configuration(final Environment environment) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        final String consumerKey = environment.getProperty("twitter.consumerKey");
        final String consumerSecret = environment.getProperty("twitter.consumerSecret");
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        return new TwitterConfiguration(cb.build());
    }
    
    @Bean
    public TwitterFactory twitterFactory(final TwitterConfiguration configuration) {
        return new TwitterFactory(configuration.getConfiguration());
    }
    
    @Bean
    public Twitter twitter(final TwitterFactory factory) {
        return factory.getInstance();
    }
    
}
