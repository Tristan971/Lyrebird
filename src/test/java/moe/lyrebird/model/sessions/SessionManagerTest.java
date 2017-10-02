package moe.lyrebird.model.sessions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionManagerTest {
    private static final Session exampleSession = new Session("fake-user", null);
    
    @Autowired
    private SessionManager sessionManager;
    
    @Test
    public void testSaveAndReload() {
        this.sessionManager.addSession(exampleSession);
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
        final Session newSession = new Session("test", null);
        this.sessionManager.addSession(newSession);
        Assert.assertEquals(newSession, this.sessionManager.getCurrentSession().getKey());
    }
    
}