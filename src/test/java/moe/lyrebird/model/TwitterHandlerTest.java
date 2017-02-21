package moe.lyrebird.model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
    public void twitterUnicity() {
        Assertions.assertNotEquals(
                this.context.getBean(Twitter.class),
                this.context.getBean(Twitter.class),
                Twitter.class.getCanonicalName() + " bean was singleton instead of prototype."
        );
    }
}