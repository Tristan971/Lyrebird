package moe.lyrebird.view.views.fxml;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.format.Tweet;
import moe.tristan.easyfxml.api.FxmlController;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tristan on 03/03/2017.
 */
@Slf4j
@Component
@Lazy
public class TimelineController implements FxmlController {

    private final TimelineManager timelineManager;

    @FXML
    private ListView<String> tweets;
    
    public TimelineController(final TimelineManager timelineManager) {
        this.timelineManager = timelineManager;
    }
    
    @Override
    public void initialize() {
        tweets.setItems(
                FXCollections.observableArrayList(
                        adaptStatuses(timelineManager.getTweets())
                )
        );
    }

    private List<String> adaptStatuses(final Collection<Status> statuses) {
        return statuses.stream()
                .map(Tweet::of)
                .collect(Collectors.toList());
    }
}
