package moe.lyrebird.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.Twitter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TwitterHandlerTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void twitterSetupTest() {
        Twitter twitter = context.getBean(Twitter.class);

        //// The factory instance is re-useable and thread safe.
        //List<Status> statuses = twitter.getHomeTimeline();
        //System.out.println("Showing home timeline.");
        //for (Status status : statuses) {
        //    System.out.println(status.getUser().getName() + ":" +
        //            status.getText());
        //}
    }

    @Test
    public void twitterSignInButtonTest() {
        // See https://dev.twitter.com/web/sign-in/implementing , https://dev.twitter.com/oauth/overview/authorizing-requests
    }
}