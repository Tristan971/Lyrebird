package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GUIManager;
import moe.tristan.easyfxml.FxmlController;
import moe.tristan.easyfxml.model.views.StageManager;
import moe.tristan.easyfxml.util.StageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static moe.lyrebird.view.views.Views.*;
import static moe.tristan.easyfxml.model.exception.ExceptionPaneBehavior.DIALOG;
import static moe.tristan.easyfxml.model.exception.ExceptionPaneBehavior.INLINE;

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
        final Pane loginPane = this.guiManager.getViewsLoader().loadPaneForView(LOGIN_VIEW, DIALOG);
        final Stage loginStage = StageUtils.stageOf("Login", loginPane);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        StageUtils.scheduleDisplaying(loginStage);
        this.stageManager.registerSingleStageController(LoginViewController.class, loginStage);
    }
    
    private void loadTimeline() {
        log.info("Loading timeline.");
        final Pane timelinePane = this.guiManager.getViewsLoader().loadPaneForView(TIMELINE_VIEW, INLINE);
        this.contentPane.getChildren().add(timelinePane);
        log.info("Loaded timeline!");
    }

    private void openTweetWindow() {
        log.info("Opening new tweet stage...");
        final Pane tweetPane = this.guiManager.getViewsLoader().loadPaneForView(TWEET_VIEW, DIALOG);
        final Stage tweetStage = StageUtils.stageOf("Tweet", tweetPane);
        tweetStage.initModality(Modality.APPLICATION_MODAL);
        StageUtils.scheduleDisplaying(tweetStage);
        this.stageManager.registerSingleStageController(TweetController.class, tweetStage);
        log.info("New tweet stage opened !");
    }
}
