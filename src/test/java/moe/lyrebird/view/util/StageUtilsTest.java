package moe.lyrebird.view.util;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import moe.lyrebird.Lombok;
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
public class StageUtilsTest extends ApplicationTest {
    @Test
    public void stageOf() throws Exception {
        Platform.runLater(() -> StageUtils.stageOf("Test", new Pane()));
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(StageUtils.class);
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.show();
    }
}