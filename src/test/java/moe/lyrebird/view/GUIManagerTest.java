package moe.lyrebird.view;

import javafx.stage.Stage;
import moe.lyrebird.view.views.Controller;
import org.junit.Assert;
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
    private Stage newStage;
    
    @Test
    public void enableAWT() throws Exception {
        GUIManager.enableAWT();
    }
    
    @Test
    public void registerStage() throws Exception {
        this.guiManager.registerStage(TestController.class, this.newStage);
        Assert.assertEquals(1, this.guiManager.getStages().size());
        Assert.assertEquals(this.newStage, this.guiManager.getStages().get(TestController.class));
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        this.guiManager.startGui(stage);
        this.newStage = new Stage();
    }
    
    private static final class TestController implements Controller {
        @Override
        public void initialize() {
        }
    }
}