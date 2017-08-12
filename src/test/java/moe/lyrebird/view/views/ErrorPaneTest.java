package moe.lyrebird.view.views;

import javafx.application.Platform;
import javafx.stage.Stage;
import moe.lyrebird.Lombok;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore
public class ErrorPaneTest extends ApplicationTest {

    @Test
    public void of() throws Exception {
        ErrorPane.of(
                "Test",
                new RuntimeException("TestException")
        );
    }
    
    @SuppressWarnings("CodeBlock2Expr")
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
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Platform.exit();
    }
}