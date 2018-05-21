package moe.lyrebird.view.views.controlbar;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsync;
import moe.tristan.easyfxml.util.Stages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.views.Views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.concurrent.CompletableFuture;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static moe.lyrebird.view.views.Views.LOGIN_VIEW;
import static moe.lyrebird.view.views.Views.TWEET_VIEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControlBarController implements FxmlController {

    @FXML
    private Label currentUser;
    @FXML
    private Button loginButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Button tweetButton;
    @FXML
    private Button errorButton;

    private final EasyFxml easyFxml;
    private final StageManager stageManager;
    private final TimelineManager timelineManager;
    private final SessionManager sessionManager;

    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_CLICKED, e -> openLoginWindow());
        this.tweetButton.addEventHandler(MOUSE_CLICKED, e -> openTweetWindow());
        this.timelineButton.addEventHandler(MOUSE_CLICKED, e -> requestTimelineRefresh());
        this.errorButton.addEventHandler(MOUSE_CLICKED, e -> showErrorTest());

        sessionManager.getCurrentSession().map(Session::getUserId)
                      .peek(value -> this.currentUser.setText(value))
                      .onEmpty(() -> this.currentUser.setText("No account yet"));
    }

    private void requestTimelineRefresh() {
        FxAsync.doOnFxThread(timelineButton, btn -> btn.setDisable(true));
        CompletableFuture.runAsync(this.timelineManager::refreshTweets)
                         .thenRunAsync(() -> FxAsync.doOnFxThread(timelineButton, btn -> btn.setDisable(false)));
    }

    private void openLoginWindow() {
        log.info("User requested login.");
        final Pane loginPane = this.easyFxml
                .loadNode(LOGIN_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        Stages.stageOf("Login", loginPane)
              .thenCompose(Stages::scheduleDisplaying)
              .thenAccept(stage -> this.stageManager.registerSingle(Views.LOGIN_VIEW, stage));
    }

    private void openTweetWindow() {
        log.info("Opening new tweet stage...");
        final Pane tweetPane = this.easyFxml
                .loadNode(TWEET_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        Stages.stageOf("Tweet", tweetPane)
              .thenCompose(Stages::scheduleDisplaying)
              .thenAccept(stage -> this.stageManager.registerSingle(TWEET_VIEW, stage))
              .thenRun(() -> log.info("New tweet stage opened !"));
    }

    private void showErrorTest() {
        ExceptionHandler.displayExceptionPane(
                "Test error",
                "Test exception msg\nline 2!!",
                new RuntimeException("Wew lad")
        );
    }
}
