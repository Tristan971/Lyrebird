package moe.lyrebird.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * Back-end (Twitter, persistence etc.) components go here.
 */
@Configuration
public class BackendComponents {
    
    @Bean(name = "twitterHandler")
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }
}
