package moe.lyrebird.view.screens.user;

import org.springframework.stereotype.Component;

import moe.lyrebird.view.screens.update.UpdateScreenController;
import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class UserScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "UserView.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return UpdateScreenController.class;
    }

}
