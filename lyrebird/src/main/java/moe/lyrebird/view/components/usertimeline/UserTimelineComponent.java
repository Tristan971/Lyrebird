package moe.lyrebird.view.components.usertimeline;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class UserTimelineComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "UserTimeline.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return UserTimelineController.class;
    }

}
