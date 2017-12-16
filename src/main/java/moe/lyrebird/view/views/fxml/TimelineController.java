package moe.lyrebird.view.views.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.format.Tweet;
import moe.tristan.easyfxml.api.FxmlController;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
    private ObservableList<String> tweetsObservableList = FXCollections.emptyObservableList();

    public TimelineController(final TimelineManager timelineManager) {
        log.debug("Initialized");
        this.timelineManager = timelineManager;
    }
    
    @Override
    public void initialize() {
        bindModel();
        bindUi();
    }

    private void bindModel() {
        log.debug("Subscribing to {}", TimelineManager.class.getSimpleName());
        timelineManager.subscribe(change -> {
            tweetsObservableList.addAll(Tweet.ofStatuses(change.getAddedSubList()));
            tweetsObservableList.removeAll(Tweet.ofStatuses(change.getRemoved()));
        });
        log.debug("Subscribed.");
    }

    private void bindUi() {
        log.debug("Binding tweets displayed to model...");
        tweets.setItems(tweetsObservableList);
        log.debug("Binded tweets.");
    }
}
