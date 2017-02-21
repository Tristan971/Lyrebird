package moe.lyrebird.model;

import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.Twitter;

/**
 * This class is a decorator around the {@link Twitter}
 * class.
 */
public class TwitterHandler {
    
    private final Twitter twitter;
    
    @Autowired
    public TwitterHandler(final Twitter twitter) {
        this.twitter = twitter;
    }
    
}
