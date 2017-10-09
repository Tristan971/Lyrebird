package moe.lyrebird.view;

import lombok.extern.slf4j.Slf4j;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.fxml.FxmlLoader;
import moe.tristan.easyfxml.spring.SpringContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.ResourceBundle;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Front-end (views-related) components go here.
 */
@Configuration
@Import(SpringContext.class)
@Slf4j
public class FrontendComponents {
    
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("strings");
    }
    
    /**
     * Containerize {@link FxmlLoader} so we can add our resource bundle.

     */
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public FxmlLoader fxmlLoader(final ApplicationContext context, final ResourceBundle resourceBundle) {
        final FxmlLoader loader = new FxmlLoader();
        loader.setControllerFactory(context::getBean);
        loader.setResources(resourceBundle);
        return loader;
    }

    @Bean
    public GUIManager guiManager(final Environment environment, final EasyFxml easyFxml, final ResourceBundle resourceBundle) {
        return new GUIManager(environment, easyFxml, resourceBundle);
    }
}
