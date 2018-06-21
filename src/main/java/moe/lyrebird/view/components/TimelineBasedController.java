package moe.lyrebird.view.components;

import org.springframework.context.ApplicationContext;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.components.listview.CustomListViewBase;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.TwitterTimelineBaseModel;
import moe.lyrebird.view.components.cells.TweetListCell;
import org.slf4j.Logger;
import twitter4j.Status;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public abstract class TimelineBasedController extends CustomListViewBase<Status> implements FxmlController {

    @FXML
    private Button loadMoreButton;

    private final TwitterTimelineBaseModel timelineBase;
    private final ListProperty<Status> tweetsProperty;

    public TimelineBasedController(
            final TwitterTimelineBaseModel timelineBase,
            final SessionManager sessionManager,
            final ApplicationContext context
    ) {
        super(context, TweetListCell.class);
        this.timelineBase = timelineBase;
        this.tweetsProperty = new ReadOnlyListWrapper<>(timelineBase.loadedTweets());
        sessionManager.currentSessionProperty().addListener(change -> timelineBase.loadLastTweets());
    }

    @Override
    public void initialize() {
        super.initialize();
        timelineBase.loadLastTweets();
        loadMoreButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> loadMoreTweets());
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(timelineBase.loadedTweets()));
    }

    private void loadMoreTweets() {
        getOldestTweetLoaded().ifPresent(oldestStatus -> {
            LOG().debug("Loading tweets before {}", oldestStatus.getId());
            timelineBase.loadMoreTweets(oldestStatus.getId());
        });
    }

    private Optional<Status> getOldestTweetLoaded() {
        if (tweetsProperty.isEmpty()) {
            LOG().debug("No older tweets to load.");
            return Optional.empty();
        }
        final Status oldest = tweetsProperty.getValue().get(tweetsProperty.size() - 1);
        LOG().debug("Loading tweets before {}", oldest.getId());
        return Optional.of(oldest);
    }


    
    protected abstract Logger LOG();

}
