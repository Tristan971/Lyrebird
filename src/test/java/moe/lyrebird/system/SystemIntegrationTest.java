package moe.lyrebird.system;

import javafx.stage.Stage;
import moe.lyrebird.lang.SneakyThrow;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.support.WaitUntilSupport;

import java.net.URL;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore(value = "I don't fucking know why but JavaFX is broken...")
public class SystemIntegrationTest extends ApplicationTest {
    
    private static final URL GOOGLE = SneakyThrow.unchecked(
            () -> new URL("https://www.google.com")
    );
    @Autowired
    private SystemIntegration systemIntegration;
    
    @Before
    public void setUp() {
        java.awt.Toolkit.getDefaultToolkit();
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.show();
    }

    @Test
    public void testOpenBrowser() {
        final WaitUntilSupport wait = new WaitUntilSupport();
        systemIntegration.openBrowser(GOOGLE);
    }
}
