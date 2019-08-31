package moe.lyrebird.view.screens.credits;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class CreditScreenComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "Credits.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return CreditsScreenController.class;
    }

}
