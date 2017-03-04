package moe.lyrebird.model.twitter4j;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

import static moe.lyrebird.model.twitter4j.TwitterHandler.FAKE_ACCESS_TOKEN;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TwitterHandlerTest {
    
    @Autowired
    private ApplicationContext context;
    @Autowired
    private TwitterHandler twitterHandler;
    
    @Test
    public void newSession() throws Exception {
        final Pair<URL, RequestToken> urlRequestTokenPair = this.twitterHandler.newSession();
        Assert.assertNotNull(urlRequestTokenPair.getFirst().toURI());
        Assert.assertNotNull(urlRequestTokenPair.getSecond());
    }
    
    @Test
    public void registerAccessToken() throws Exception {
        final Optional<AccessToken> accessToken = this.twitterHandler.registerAccessToken(
                new RequestToken("fake", "token"),
                "1234"
        );
        Assert.assertFalse(accessToken.isPresent());
    }
    
    @Test
    public void getTwitter() throws Exception {
        Assert.assertNotNull(this.twitterHandler.getTwitter());
    }
    
    @Test
    public void getAccessToken() throws Exception {
        this.twitterHandler = this.context.getBean(TwitterHandler.class);
        Assert.assertNotNull(this.twitterHandler.getAccessToken());
        Assert.assertEquals(this.twitterHandler.getAccessToken(), FAKE_ACCESS_TOKEN);
    }
    
}