package moe.lyrebird.view.screens.login;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class LoginScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "Login.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return LoginScreenController.class;
    }

}
