package moe.lyrebird.view.views;

import de.saxsys.javafx.test.JfxRunner;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import moe.lyrebird.Lombok;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(JfxRunner.class)
public class ErrorPaneTest {
    
    @Before
    public void setUp() {
        new JFXPanel();
    }
    
    @Test
    public void of() throws Exception {
        ErrorPane.of(
                "Test",
                new RuntimeException("TestException")
        );
    }
    
    @Test
    public void displayErrorPaneOf() throws Exception {
        Platform.runLater(() -> {
            ErrorPane.displayErrorPaneOf(
                    "Test",
                    new RuntimeException("TestException")
            );
        });
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(ErrorPane.class);
    }
    
}