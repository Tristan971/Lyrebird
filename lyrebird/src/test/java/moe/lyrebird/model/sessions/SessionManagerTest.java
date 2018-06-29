package moe.lyrebird.model.sessions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionManagerTest {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ApplicationContext context;

    private Session exampleSession;

    @Before
    public void setUp() {
        exampleSession = new Session(
                "fake-user",
                null,
                context.getBean(TwitterHandler.class)
        );
    }

    @Test
    public void testSaveAndReload() {
        this.sessionManager.loadSession(exampleSession);
        this.sessionManager.saveAllSessions();
        this.sessionManager.reloadAllSessions();
        assertThat(this.sessionManager.getSessionForUser(exampleSession.getUserId()).isPresent()).isTrue();
    }

}
