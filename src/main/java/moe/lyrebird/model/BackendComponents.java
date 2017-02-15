package moe.lyrebird.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
     * @return a {@link Configuration}
     */
    @Bean
    public twitter4j.conf.Configuration configuration(final Environment environment) {
        ConfigurationBuilder cb = new ConfigurationBuilder();

        String consumerKey = environment.getProperty("twitter.consumerKey");
        String consumerSecret = environment.getProperty("twitter.consumerSecret");
        String accessToken = "";
        String accessTokenSecret = "";

        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setOAuthAccessToken(accessToken);

        return cb.build();
    }

    @Bean
    public TwitterFactory twitterFactory(twitter4j.conf.Configuration configuration) {
        return new TwitterFactory(configuration);
    }

    @Bean
    public Twitter twitter(TwitterFactory factory) {
        return factory.getInstance();
    }

    @Bean
    public TwitterHandler twitterHandler(Twitter twitter) {
        return new TwitterHandler(twitter);
    }
}
