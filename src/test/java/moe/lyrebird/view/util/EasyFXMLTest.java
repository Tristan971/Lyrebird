package moe.lyrebird.view.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

/**
 * Created by Tristan on 06/02/2017.
 */
class EasyFXMLTest {
    @Test
    void getPaneForView() {
    }
    
    @Test
    void getPaneForFile() {
    }
    
    @Test
    void getURLForView() {
        final URL rootViewURL = EasyFXML.getURLForView("moe/lyrebird/view/views/fxml/RootView.fxml");
        Assertions.assertNotNull(rootViewURL, "The root view FXML file was not found!");
    }
    
}