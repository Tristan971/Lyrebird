package moe.lyrebird.view.views.fxml;

import io.vavr.control.Option;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.view.format.Tweet;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;

import java.util.Map;

/**
 * Created by tristan on 03/03/2017.
 */
@Slf4j
@Component
@Lazy
public class TimelineController implements FxmlController {
    private final Option<TwitterHandler> twitterHandler;
    @FXML
    private ListView<String> tweets;
    
    public TimelineController(final SessionManager sessionManager) {
        this.twitterHandler = Option.of(sessionManager.getCurrentSession()).map(Map.Entry::getValue);
    }
    
    @Override
    public void initialize() {
        this.updateTimeline();
    }
    
    private void updateTimeline() {
        this.twitterHandler.toTry(IllegalStateException::new)
                .map(TwitterHandler::getTwitter)
                .mapTry(Twitter::getHomeTimeline)
                .map(Tweet::ofStatuses)
                .map(FXCollections::observableArrayList)
                .onFailure(this::onTimelineRefreshError)
                .onSuccess(this.tweets::setItems);
    }
    
    private void onTimelineRefreshError(final Throwable reason) {
        ExceptionHandler.displayExceptionPane(
                "An unexpected error occured",
                "Could not get your timeline.",
                reason
        );
    }
}
