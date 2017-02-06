package moe.lyrebird.view;

import javafx.fxml.FXMLLoader;
import moe.lyrebird.view.util.EasyFXML;
import moe.lyrebird.view.util.ViewLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Front-end (views-related) components go here.
 */
@Configuration
public class FrontendComponents {
    
    @Bean
    public FXMLLoader fxmlLoader(final ApplicationContext context) {
        final FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        return loader;
    }
    
    @Bean
    public EasyFXML easyFXML(final FXMLLoader fxmlLoader) {
        return new EasyFXML(fxmlLoader);
    }
    
    @Bean
    public ViewLoader viewLoader(final EasyFXML easyFXML) {
        return new ViewLoader(easyFXML);
    }
    
    @Bean
    public GUIManager guiManager(final ViewLoader viewLoader) {
        return new GUIManager(viewLoader);
    }
    
}
