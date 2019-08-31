package moe.lyrebird.view.components.notifications;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class NotificationPaneComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "NotificationPane.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return NotificationsController.class;
    }

}
