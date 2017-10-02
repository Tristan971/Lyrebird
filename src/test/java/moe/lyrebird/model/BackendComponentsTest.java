package moe.lyrebird.model;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.auth.AccessToken;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BackendComponentsTest {
    @Autowired
    private ApplicationContext context;
    
    @Test
    public void twitterHandler() {
        final TwitterHandler twitterHandler1 = this.context.getBean(TwitterHandler.class);
        twitterHandler1.setAccessToken(new AccessToken("some", "token"));
        final TwitterHandler twitterHandler2 = this.context.getBean(TwitterHandler.class);
        
        Assert.assertNotEquals(twitterHandler1, twitterHandler2);
    }
    
    @Test
    public void sessionManager() {
        Assert.assertEquals(this.context.getBean(SessionManager.class), this.context.getBean(SessionManager.class));
    }
    
}