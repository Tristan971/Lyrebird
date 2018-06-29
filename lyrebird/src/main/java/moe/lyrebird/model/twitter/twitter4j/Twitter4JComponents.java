package moe.lyrebird.model.twitter.twitter4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by Tristan on 22/02/2017.
 */
@Configuration
public class Twitter4JComponents {
    @Bean
    public twitter4j.conf.Configuration configuration(final Environment environment) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        final String consumerKey = environment.getProperty("twitter.consumerKey");
        final String consumerSecret = environment.getProperty("twitter.consumerSecret");
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        return cb.build();
    }

    @Bean
    public TwitterFactory twitterFactory(final twitter4j.conf.Configuration configuration) {
        return new TwitterFactory(configuration);
    }

    @Bean
    @Scope(value = SCOPE_PROTOTYPE)
    public Twitter twitter(final TwitterFactory factory) {
        return factory.getInstance();
    }

    @Bean
    public TwitterStreamFactory twitterStreamFactory(final twitter4j.conf.Configuration configuration) {
        return new TwitterStreamFactory(configuration);
    }

    @Bean
    public TwitterStream twitterStream(final TwitterStreamFactory streamFactory) {
        return streamFactory.getInstance();
    }

}
