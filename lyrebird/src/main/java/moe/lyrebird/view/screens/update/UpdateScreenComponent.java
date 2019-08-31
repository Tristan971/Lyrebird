package moe.lyrebird.view.screens.update;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class UpdateScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "Update.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return UpdateScreenController.class;
    }

}
