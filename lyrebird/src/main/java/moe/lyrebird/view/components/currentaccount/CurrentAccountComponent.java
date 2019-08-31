package moe.lyrebird.view.components.currentaccount;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class CurrentAccountComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "CurrentAccount.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return CurrentAccountController.class;
    }

}
