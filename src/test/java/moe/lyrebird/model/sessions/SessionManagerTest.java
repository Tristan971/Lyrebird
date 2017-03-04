package moe.lyrebird.model.sessions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.auth.AccessToken;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionManagerTest {
    private static final Session exampleSession = Session.builder()
            .accessToken(new AccessToken("fake", "token"))
            .userId("fakeTwitterUser")
            .build();
    
    @Autowired
    private SessionManager sessionManager;
    
    private void addExampleSession() {
        this.sessionManager.addSession(exampleSession);
    }
    
    @Test
    public void testSaveAndReload() throws Exception {
        this.addExampleSession();
        this.sessionManager.saveAllSessions();
        this.sessionManager.reloadAllSessions();
        Assert.assertTrue(
                this.sessionManager
                        .getSession(exampleSession.getUserId())
                        .isPresent()
        );
    }
    
    @Test
    public void testAutosetSession() {
        final Session newSession = new Session();
        this.sessionManager.addSession(newSession);
        Assert.assertEquals(newSession, this.sessionManager.getCurrentSession().getKey());
    }
    
}