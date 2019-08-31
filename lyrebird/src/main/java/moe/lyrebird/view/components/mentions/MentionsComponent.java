package moe.lyrebird.view.components.mentions;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class MentionsComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "Mentions.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return MentionsController.class;
    }

}
