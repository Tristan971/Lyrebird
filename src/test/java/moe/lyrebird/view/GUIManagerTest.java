package moe.lyrebird.view;

import javafx.stage.Stage;
import moe.tristan.easyfxml.util.FxAsyncUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Created by Tristan on 08/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GUIManagerTest extends ApplicationTest {

    @Autowired
    private GUIManager guiManager;

    @Test
    public void testMainlaunch() {
        FxAsyncUtils.doOnFxThread(guiManager, manager -> manager.startGui(new Stage()));
    }

    @Override
    public void start(final Stage stage) {
        // JavaFX initialized
    }
}
