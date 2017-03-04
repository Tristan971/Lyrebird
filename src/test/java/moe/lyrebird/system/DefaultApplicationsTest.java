package moe.lyrebird.system;

import javafx.application.Platform;
import javafx.stage.Stage;
import moe.lyrebird.Lombok;
import moe.lyrebird.lang.SneakyThrow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DefaultApplicationsTest extends ApplicationTest {
    
    private static final URL GOOGLE = SneakyThrow.unchecked(
            () -> new URL("https://www.google.com")
    );
    
    @Before
    public void setUp() {
        java.awt.Toolkit.getDefaultToolkit();
    }
    
    @Test
    public void enabledMode() throws Exception {
        Platform.runLater(() -> DefaultApplications.openBrowser(GOOGLE));
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(DefaultApplications.class);
    }
    
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.show();
    }
}