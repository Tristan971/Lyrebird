package moe.lyrebird.view.screens.media.display.photo;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class ImageScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "ImageScreen.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return ImageScreenController.class;
    }

}
