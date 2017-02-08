package moe.lyrebird.view.util;

import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertNotNull(
                this.easyFXML,
                "Could not load bean EasyFXML!" + this.easyFXML
        );
    }
    
    @Test
    public void getPaneForView() {
        Assertions.assertTrue(
                this.easyFXML.getPaneForView(ROOT_VIEW).isSuccess(),
                "Could not get the pane for view root view!"
        );
    }
    
    @Test
    public void getPaneForFile() {
        Assertions.assertTrue(
                this.easyFXML.getPaneForFile(VIEWS_ROOT_FOLDER.toString() + ROOT_VIEW.toString()).isSuccess(),
                "Could not load the Pane for the root view!"
        );
    }
    
    @Test
    public void getURLForView() {
        Assertions.assertNotNull(
                EasyFXML.getURLForView(VIEWS_ROOT_FOLDER.toString() + ROOT_VIEW.toString()),
                "Could not get the URL for root view!"
        );
    }
    
}