package moe.lyrebird.view.util;

import javafx.embed.swing.JFXPanel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static moe.lyrebird.view.views.Views.ROOT_VIEW;
import static moe.lyrebird.view.views.Views.VIEWS_ROOT_FOLDER;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EasyFXMLTest {
    @Autowired
    private EasyFXML easyFXML;

    @Before
    public void testAutowire() {
        new JFXPanel();
        Assert.assertNotNull(
                "Could not load bean EasyFXML!" + this.easyFXML,
                this.easyFXML
        );
    }

    @Test
    public void getPaneForView() {
        Assert.assertTrue(
                "Could not get the pane for view root view!",
                this.easyFXML.getPaneForView(ROOT_VIEW).isSuccess()
        );
    }

    @Test
    public void getPaneForFile() {
        Assert.assertTrue(
                "Could not load the Pane for the root view!",
                this.easyFXML.getPaneForFile(VIEWS_ROOT_FOLDER.toString() + ROOT_VIEW.toString()).isSuccess()
        );
    }
    
    @Test
    public void getPaneForNoFile() throws Exception {
        this.easyFXML.getPaneForFile("brokenfxml.fxml");
    }

    @Test
    public void getURLForView() {
        Assert.assertNotNull(
                "Could not get the URL for root view!",
                EasyFXML.getURLForView(VIEWS_ROOT_FOLDER.toString() + ROOT_VIEW.toString())
        );
    }

}