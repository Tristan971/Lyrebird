package moe.lyrebird.model.sessions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionManagerTest {
    private static final Session exampleSession = Session.builder()
            .oAuth2Authorization(null)
            .uid(-1L)
            .userHandle("TestHandle")
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
        assertTrue(this.sessionManager.getSession(exampleSession.getUserHandle()).isPresent());
    }
    
}