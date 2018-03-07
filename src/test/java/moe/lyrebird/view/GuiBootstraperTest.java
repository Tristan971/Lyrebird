package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import moe.tristan.easyfxml.util.FxAsync;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

/**
 * Created by Tristan on 08/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GuiBootstraperTest extends ApplicationTest {

    @Autowired
    private GuiBootstraper guiBootstraper;

    @Test
    public void testMainlaunch() {
        FxAsync.doOnFxThread(guiBootstraper, manager -> manager.startGui(new Stage()));
    }

    @Override
    public void start(final Stage stage) {
        // JavaFX initialized
    }
}
