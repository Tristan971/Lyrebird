package moe.lyrebird.view.components.timeline;

import org.springframework.stereotype.Component;

import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;

@Component
public class TimelineComponent implements FxmlComponent {

    @Override
    public FxmlFile getFile() {
        return () -> "Timeline.fxml";
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return TimelineController.class;
    }

}
