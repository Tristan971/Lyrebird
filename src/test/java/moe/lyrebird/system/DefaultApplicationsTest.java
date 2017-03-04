package moe.lyrebird.system;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import moe.lyrebird.Lombok;
import moe.lyrebird.lang.SneakyThrow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(JfxRunner.class)
public class DefaultApplicationsTest {
    
    private static final URL GOOGLE = SneakyThrow.unchecked(
            () -> new URL("https://www.google.com")
    );
    
    @Before
    public void setUp() {
        java.awt.Toolkit.getDefaultToolkit();
    }
    
    @Test(expected = IllegalStateException.class)
    public void headlessMode() throws Exception {
        DefaultApplications.openBrowser(GOOGLE);
    }
    
    @Test
    @TestInJfxThread
    public void enabledMode() throws Exception {
        DefaultApplications.openBrowser(GOOGLE);
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(DefaultApplications.class);
    }
    
    
}