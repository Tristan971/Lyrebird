package moe.lyrebird.model.sessions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tristan on 03/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionTest {
    private final Session session = new Session();

    @Test(expected = NullPointerException.class)
    public void setUserId() throws Exception {
        this.session.setUserId(null);
    }

    @Test(expected = NullPointerException.class)
    public void setAccessToken() throws Exception {
        this.session.setAccessToken(null);
    }

}