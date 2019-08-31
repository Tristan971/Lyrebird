package moe.lyrebird.view.components.tweet;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class TweetInteractionPaneComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "TweetInteractionPane.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return TweetInteractionPaneController.class;
    }

}
