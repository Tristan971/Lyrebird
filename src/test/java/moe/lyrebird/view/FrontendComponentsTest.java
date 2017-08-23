package moe.lyrebird.view;

import javafx.fxml.FXMLLoader;
import moe.lyrebird.view.util.EasyFXML;
import moe.lyrebird.view.util.ViewLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Tristan on 08/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FrontendComponentsTest {

    @Autowired
    private ApplicationContext context;
    
    /*
    Use Object#hashCode here rather than #equals since FXMLLoader overrides
    equals to set it to point to FXMLLoader#location rather than itself.
    Yes it breaks the Equals/HashCode contract. Ask Oracle about why, I didn't
    write it myself.
     */
    @Test
    public void fxmlLoader() throws Exception {
        final FXMLLoader fxmlLoader = this.context.getBean(FXMLLoader.class);
        final FXMLLoader fxmlLoader1 = this.context.getBean(FXMLLoader.class);
        Assert.assertNotEquals(
                "The FXMLLoader must not be a singleton bean since it can't be reused.",
                fxmlLoader.hashCode(),
                fxmlLoader1
        );
    }
    
    @Test
    public void easyFXML() throws Exception {
        Assert.assertEquals(
                "The EasyFXML is reusable and should thus be reused if possible.",
                this.context.getBean(EasyFXML.class),
                this.context.getBean(EasyFXML.class)
        );
    }
    
    @Test
    public void viewLoader() throws Exception {
        Assert.assertEquals(
                "The ViewLoader is reusable and should thus be reused if possible.",
                this.context.getBean(ViewLoader.class),
                this.context.getBean(ViewLoader.class)
        );
    }
    
    @Test
    public void guiManager() throws Exception {
    }
    
}