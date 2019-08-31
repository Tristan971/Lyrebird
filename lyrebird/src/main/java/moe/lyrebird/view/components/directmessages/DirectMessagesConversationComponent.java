package moe.lyrebird.view.components.directmessages;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class DirectMessagesConversationComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "DMConversation.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return DMConversationController.class;
    }

}
