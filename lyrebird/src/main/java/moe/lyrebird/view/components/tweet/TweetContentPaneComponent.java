package moe.lyrebird.view.components.tweet;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class TweetContentPaneComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "TweetContentPane.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return TweetContentPaneController.class;
    }

}
