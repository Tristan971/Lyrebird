package moe.lyrebird.view.views.fxml;

import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.view.format.Tweet;
import moe.tristan.easyfxml.FxmlController;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

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
        try {
            List<String> statuses = this.twitterHandler.map(TwitterHandler::getTwitter)
                    .map(unchecked((CheckedFunction1<Twitter,ResponseList<Status>>) (Twitter::getHomeTimeline)))
                    .toStream()
                    .getOrElseThrow(IllegalStateException::new)
                    .stream()
                    .map(Tweet::of)
                    .collect(Collectors.toList());

            log.info("Loaded {} new statuses", statuses.size());

            this.tweets.setItems(FXCollections.observableArrayList(statuses));
        } catch (final IllegalStateException e) {
            this.tweets.setItems(FXCollections.observableArrayList("You are not logged in ! Please click login."));
        }
    }
}
