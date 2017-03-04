package moe.lyrebird.view.util;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import moe.lyrebird.Lombok;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(JfxRunner.class)
public class StageUtilsTest {
    @Test
    @TestInJfxThread
    public void stageOf() throws Exception {
        final Stage stage = StageUtils.stageOf("Test", new Scene(new Pane()));
        stage.hide();
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(StageUtils.class);
    }
    
}