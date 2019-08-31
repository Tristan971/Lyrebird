package moe.lyrebird.view.screens.newtweet;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class NewTweetScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "NewTweet.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return NewTweetController.class;
    }

}
