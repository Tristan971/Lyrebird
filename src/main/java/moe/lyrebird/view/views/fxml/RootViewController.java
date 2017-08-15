package moe.lyrebird.view.views.fxml;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GUIManager;
import moe.lyrebird.view.util.StageUtils;
import moe.lyrebird.view.views.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static moe.lyrebird.view.views.Views.*;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@Component
@Slf4j
public class RootViewController implements Controller {
    private final GUIManager guiManager;

    @FXML
    private Button loginButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Button tweetButton;
    @FXML
    private Pane contentPane;
    
    @Autowired
    public RootViewController(final ApplicationContext context) {
        this.guiManager = context.getBean(GUIManager.class);
    }
    
    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_RELEASED, event -> this.openLoginWindow());
        this.timelineButton.addEventHandler(MOUSE_RELEASED, event -> this.loadTimeline());
        this.tweetButton.addEventHandler(MOUSE_RELEASED, event -> this.openTweetWindow());
    }
    
    private void openLoginWindow() {
        log.info("User requested login.");
        final Pane loginPane = this.guiManager.getViewLoader().loadPane(LOGIN_VIEW);
        final Stage loginStage = StageUtils.stageOf("Login", loginPane);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        Platform.runLater(loginStage::show);
        this.guiManager.registerStage(LoginViewController.class, loginStage);
    }
    
    private void loadTimeline() {
        log.info("Loading timeline.");
        final Pane timelinePane = this.guiManager.getViewLoader().loadPane(TIMELINE_VIEW);
        this.contentPane.getChildren().add(timelinePane);
        log.info("Loaded timeline!");
    }

    private void openTweetWindow() {
        log.info("Opening new tweet stage...");
        final Pane tweetPane = this.guiManager.getViewLoader().loadPane(TWEET_VIEW);
        final Stage tweetStage = StageUtils.stageOf("Tweet", tweetPane);
        tweetStage.initModality(Modality.APPLICATION_MODAL);
        Platform.runLater(tweetStage::show);
        this.guiManager.registerStage(TweetController.class, tweetStage);
        log.info("New tweet stage opened !");
    }
}
