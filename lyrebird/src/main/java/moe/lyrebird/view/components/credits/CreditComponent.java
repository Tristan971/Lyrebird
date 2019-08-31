package moe.lyrebird.view.components.credits;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class CreditComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "CreditPane.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return CreditController.class;
    }

}
