package moe.lyrebird.model.twitter4j;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

/**
 * Created by tristan on 02/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore
public class TwitterConfigurationTest {

    @Autowired
    private ApplicationContext context;

    //@Test(expected = NullPointerException.class)
    //public void badUid() throws Exception {
    //    final TwitterConfiguration configuration = this.context.getBean(TwitterConfiguration.class);
    //    configuration.setUid(null);
    //}

    @Test(expected = NullPointerException.class)
    public void badConfiguration() throws Exception {
        final TwitterConfiguration configuration = TwitterConfiguration.builder()
                .uid((long) -1)
                .configuration(null)
                .build();
    }

    @Test
    public void noNullity() throws Exception {
        final TwitterConfiguration configuration = this.context.getBean(TwitterConfiguration.class);
        Objects.requireNonNull(configuration.getConfiguration());
        Objects.requireNonNull(configuration.getUid());
    }

}