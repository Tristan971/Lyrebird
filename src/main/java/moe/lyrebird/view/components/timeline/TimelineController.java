package moe.lyrebird.view.components.timeline;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.components.cells.TweetListCell;
import twitter4j.Status;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;

import java.util.function.Supplier;

/**
 * Created by tristan on 03/03/2017.
 */
@Slf4j
@Component
public class TimelineController implements FxmlController {

    @FXML
    private ListView<Status> tweetsListView;

    private final TimelineManager timelineManager;
    private final Supplier<TweetListCell> tweetListCell;
    private final ListProperty<Status> tweetsProperty;

    public TimelineController(TimelineManager timelineManager, ApplicationContext context) {
        this.timelineManager = timelineManager;
        this.tweetsProperty = new ReadOnlyListWrapper<>(timelineManager.getLoadedTweets());
        this.tweetListCell = () -> context.getBean(TweetListCell.class);
    }

    @Override
    public void initialize() {
        bindUi();
        autoloadMoreTweets();
    }

    private void bindUi() {
        tweetsListView.setCellFactory(statuses -> tweetListCell.get());
        log.debug("Binding displayed tweets to displayable tweets...");
        tweetsListView.itemsProperty().bind(tweetsProperty);
        log.debug("Binded.");
    }

    /**
     * Stupid hack pls fix JavaFX we shouldn't need this...
     */
    @SuppressWarnings("unchecked")
    private void autoloadMoreTweets() {
        tweetsListView.addEventFilter(ScrollEvent.SCROLL, event -> {
            final ListViewSkin<Status> ts = (ListViewSkin<Status>) tweetsListView.getSkin();
            final VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            final int lastVisible = vf.getLastVisibleCell().getIndex();
            final int lastPossible = tweetsListView.getItems().size() - 1;
            final boolean scrolledToEnd = lastVisible == lastPossible;
            if (scrolledToEnd) {
                log.debug("Scrolled to end [{}/{}]. Requesting more tweets.", lastVisible, lastPossible);
                timelineManager.loadMoreTweets();
            }
        });
    }

}
