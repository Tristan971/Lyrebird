package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMConversationController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(DMConversationController.class);

    private User pal;

    private final DirectMessages directMessages;

    public DMConversationController(DirectMessages directMessages) {
        this.directMessages = directMessages;
    }

    @Override
    public void initialize() {
        LOG.debug("Created conversation instance for conversing with user {} [{}]", pal.getScreenName(), pal);
    }

    public void setPal(User pal) {
        this.pal = pal;
    }

}
