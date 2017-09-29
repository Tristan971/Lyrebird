package moe.lyrebird.view;

import javafx.stage.Stage;
import moe.tristan.easyfxml.FxmlController;
import org.junit.Ignore;
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
@Ignore
public class GUIManagerTest extends ApplicationTest {
    @Autowired
    private GUIManager guiManager;
    private Stage newStage;
    
    @Test
    public void enableAWT() {
        GUIManager.enableAWT();
    }
    
    @Override
    public void start(final Stage stage) {
        this.guiManager.startGui(stage);
        this.newStage = new Stage();
    }
}