package moe.lyrebird.view.components.tweet;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetInterractionPaneController implements FxmlController {

    @Override
    public void initialize() {

    }

}
