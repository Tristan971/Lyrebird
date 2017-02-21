package moe.lyrebird;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LyrebirdTests {
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private Environment environment;
    
    @Test
    public void contextLoads() {
        Assertions.assertNotNull(this.context);
    }
    
    @Test
    public void propertiesSetup() {
        final String version = this.environment.getProperty("app.version");
        Assertions.assertNotNull(
                version,
                "The application.properties file was not loaded at runtime."
        );
    }
}
