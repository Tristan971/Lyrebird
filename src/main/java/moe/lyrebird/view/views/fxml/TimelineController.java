package moe.lyrebird.view.views.fxml;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.view.format.Tweet;
import moe.lyrebird.view.views.Controller;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tristan on 03/03/2017.
 */
@Slf4j
@Component
@Lazy
public class TimelineController implements Controller {
    private final TwitterHandler twitterHandler;
    @FXML
    private ListView<String> tweets;
    
    public TimelineController(final SessionManager sessionManager) {
        this.twitterHandler = sessionManager.getCurrentSession().getValue();
    }
    
    @Override
    public void initialize() {
        this.updateTimeline();
    }
    
    private void updateTimeline() {
        List<Status> statuses;
        try {
            statuses = this.twitterHandler.getTwitter().getHomeTimeline();
        } catch (final TwitterException e) {
            log.error("Could not load timeline!", e);
            statuses = Collections.emptyList();
        }
        
        log.info("Loaded {} new statuses", statuses.size());
    
        final List<String> statusesStr = statuses.stream()
                .map(Tweet::of)
                .collect(Collectors.toList());
    
        this.tweets.setItems(FXCollections.observableArrayList(statusesStr));
    }
}
