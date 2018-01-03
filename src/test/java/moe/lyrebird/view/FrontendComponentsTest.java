package moe.lyrebird.view;

import javafx.fxml.FXMLLoader;
import moe.tristan.easyfxml.EasyFxml;
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

    /**
     * Use Object#hashCode here rather than #equals since FXMLLoader overrides
     * equals to set it to point to FXMLLoader#location rather than itself.
     * Yes it breaks the Equals/HashCode contract. Ask Oracle about why, I didn't
     * write it myself.
     */
    @Test
    public void fxmlLoader() {
        final FXMLLoader fxmlLoader = this.context.getBean(FXMLLoader.class);
        final FXMLLoader fxmlLoader1 = this.context.getBean(FXMLLoader.class);
        Assert.assertNotEquals(
                "The FXMLLoader must not be a singleton bean since it can't be reused.",
                fxmlLoader.hashCode(),
                fxmlLoader1
        );
    }
    
    @Test
    public void easyFXML() {
        Assert.assertEquals(
                "The EasyFXML is reusable and should thus be reused if possible.",
                this.context.getBean(EasyFxml.class),
                this.context.getBean(EasyFxml.class)
        );
    }
    
}
