package moe.lyrebird.view.util;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore(value = "I don't fucking know why but JavaFX is broken...")
public class StageUtilsTest extends ApplicationTest {
    @Override
    public void start(final Stage stage) throws Exception {
        StageUtils.stageOf("Test", new Pane()).show();
    }
}