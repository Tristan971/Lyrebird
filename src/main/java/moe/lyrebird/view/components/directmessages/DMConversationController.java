package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import twitter4j.User;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMConversationController implements FxmlController {

    private User pal;

    @Override
    public void initialize() {

    }

    public void setPal(User pal) {
        this.pal = pal;
    }

}
