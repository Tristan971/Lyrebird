package moe.lyrebird.view.screens.root;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class RootScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "RootView.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return RootScreenController.class;
    }

}
