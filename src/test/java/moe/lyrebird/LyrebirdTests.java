package moe.lyrebird;

import moe.lyrebird.model.BackendComponents;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LyrebirdTests {
    
    @Autowired
    private ApplicationContext context;
    
    @Test
    public void contextLoads() {
        Assertions.assertNotNull(this.context);
    }

    @Test
    public void twitterSetupTest() throws twitter4j.TwitterException {
        BackendComponents be = new BackendComponents();

        Twitter twitter = be.twitter();
        // The factory instance is re-useable and thread safe.
        List<Status> statuses = twitter.getHomeTimeline();
        System.out.println("Showing home timeline.");
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }
    }

    @Test
    public void twitterSignInButtonTest() {
        // See https://dev.twitter.com/web/sign-in/implementing , https://dev.twitter.com/oauth/overview/authorizing-requests
    }
}
