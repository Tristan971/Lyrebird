package moe.lyrebird.model;

import moe.lyrebird.model.twitter.TwitterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 */
@Configuration
public class BackendComponents {
    
    @Bean
    public TwitterHandler twitterHandler() {
        return new TwitterHandler();
    }
}
