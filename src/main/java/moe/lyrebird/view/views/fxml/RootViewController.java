package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GUIManager;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.model.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.StageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static moe.lyrebird.view.views.Views.*;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@Component
@Slf4j
public class RootViewController implements FxmlController {
    private final GUIManager guiManager;
    private final StageManager stageManager;

    @FXML
    private Button loginButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Button tweetButton;
    @FXML
    private Pane contentPane;
    
    @Autowired
    public RootViewController(final GUIManager guiManager, final StageManager stageManager) {
        this.guiManager = guiManager;
        this.stageManager = stageManager;
    }
    
    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_RELEASED, event -> this.openLoginWindow());
        this.timelineButton.addEventHandler(MOUSE_RELEASED, event -> this.loadTimeline());
        this.tweetButton.addEventHandler(MOUSE_RELEASED, event -> this.openTweetWindow());
    }
    
    private void openLoginWindow() {
        log.info("User requested login.");
        final Pane loginPane = this.guiManager.getEasyFxml()
                .loadNode(LOGIN_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        StageUtils.stageOf("Login", loginPane)
                .thenCompose(StageUtils::scheduleDisplaying)
                .thenAccept(stage -> this.stageManager.registerSingle(Views.LOGIN_VIEW, stage));
    }
    
    private void loadTimeline() {
        log.info("Loading timeline.");
        final Pane timelinePane = this.guiManager.getEasyFxml()
                .loadNode(TIMELINE_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        this.contentPane.getChildren().add(timelinePane);
        log.info("Loaded timeline!");
    }

    private void openTweetWindow() {
        log.info("Opening new tweet stage...");
        final Pane tweetPane = this.guiManager.getEasyFxml()
                .loadNode(TWEET_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        StageUtils.stageOf("Tweet", tweetPane)
                .thenCompose(StageUtils::scheduleDisplaying)
                .thenAccept(stage -> this.stageManager.registerSingle(TWEET_VIEW, stage))
                .thenRun(() -> log.info("New tweet stage opened !"));
    }
}
