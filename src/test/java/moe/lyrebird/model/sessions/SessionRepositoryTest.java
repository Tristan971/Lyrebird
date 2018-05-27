package moe.lyrebird.model.sessions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.auth.AccessToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(SessionRepositoryTest.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ApplicationContext context;

    private Session session = null;

    @Before
    public void setUp() {
        session = new Session(
                "test_uid",
                new AccessToken("test_token", "test_token_secret", 1010),
                context.getBean(TwitterHandler.class)
        );
    }

    @Test
    public void testSave() {
        final Session saved = sessionRepository.save(session);
        final Optional<Session> sessionTryFind = sessionRepository.findById("test_uid");
        assertThat(sessionTryFind).isPresent();
        assertThat(sessionTryFind).hasValue(saved);
        LOG.info(
                "Saved sessions : {}",
                sessionRepository.findAll().toString()
        );
    }

    @After
    public void cleanUp() {
        sessionRepository.deleteAll();
    }

}
