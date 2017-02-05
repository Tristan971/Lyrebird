package moe.lyrebird.model;

import moe.lyrebird.model.twitter.TwitterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Tristan on 05/02/2017.
 */
@Configuration
public class Services {
    
    @Bean
    public TwitterHandler twitterHandler() {
        return new TwitterHandler();
    }
}
