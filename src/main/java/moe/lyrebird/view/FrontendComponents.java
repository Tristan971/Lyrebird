package moe.lyrebird.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import moe.tristan.easyfxml.model.fxml.FxmlLoader;
import moe.tristan.easyfxml.spring.SpringContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Front-end (views-related) components go here.
 */
@Configuration
@Import(SpringContext.class)
@Slf4j
public class FrontendComponents {

    /**
     * Containerize {@link FxmlLoader} so we can add our resource bundle.
     */
    //@Bean
    //@Scope(scopeName = SCOPE_PROTOTYPE)
    //public FxmlLoader fxmlLoader(final ApplicationContext context) {
    //    final FxmlLoader loader = new FxmlLoader();
    //    loader.setControllerFactory(context::getBean);
    //    return loader;
    //}

}
