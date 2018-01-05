package moe.lyrebird.view.views.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.cells.SimpleStatusListCell;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.util.FxAsync;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.LinkedList;

/**
 * Created by tristan on 03/03/2017.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimelineController implements FxmlController {

    @FXML
    private ListView<Status> tweets;

    private final TimelineManager timelineManager;
    private final ObservableList<Status> tweetsObservableList = FXCollections.observableList(new LinkedList<>());

    @Override
    public void initialize() {
        tweets.setCellFactory(statuses -> new SimpleStatusListCell());
        bindModel();
        bindUi();
    }

    private void bindModel() {
        log.debug("Subscribing to {}", TimelineManager.class.getSimpleName());
        timelineManager.subscribe(change ->
                FxAsync.doOnFxThread(tweetsObservableList, list -> {
                    list.add(0, change.getElementAdded());
                    list.remove(change.getElementRemoved());
                })
        );
        log.debug("Subscribed.");
    }

    private void bindUi() {
        log.debug("Binding tweets displayed to model...");
        tweets.setItems(tweetsObservableList);
        log.debug("Binded tweets.");
    }
}
