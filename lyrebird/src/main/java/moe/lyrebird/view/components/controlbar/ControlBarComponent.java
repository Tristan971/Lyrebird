package moe.lyrebird.view.components.controlbar;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class ControlBarComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "ControlBar.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return ControlBarController.class;
    }

}
