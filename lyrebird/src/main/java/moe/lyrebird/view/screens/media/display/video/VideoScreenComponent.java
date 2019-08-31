package moe.lyrebird.view.screens.media.display.video;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class VideoScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "VideoScreen.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return VideoScreenController.class;
    }

}
