package moe.lyrebird.system;

import javafx.application.Platform;
import javafx.stage.Stage;
import moe.lyrebird.lang.SneakyThrow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.net.URL;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
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
    
    @Test
    public void enabledMode() throws Exception {
        Platform.runLater(() -> this.systemIntegration.openBrowser(GOOGLE));
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.show();
    }
}