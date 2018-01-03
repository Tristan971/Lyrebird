package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.tweets.TimelineManager;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsyncUtils;
import moe.tristan.easyfxml.util.StageUtils;
import org.springframework.stereotype.Component;

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

    private final EasyFxml easyFxml;
    private final StageManager stageManager;
    private final TimelineManager timelineManager;
    private final SessionManager sessionManager;

    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_CLICKED, event -> this.openLoginWindow());
        this.tweetButton.addEventHandler(MOUSE_CLICKED, event -> this.openTweetWindow());
        this.timelineButton.addEventHandler(MOUSE_CLICKED, event -> this.requestTimelineRefresh());
        sessionManager.getCurrentSession().map(Session::getUserId)
                .peek(this.currentUser::setText)
                .onEmpty(() -> this.currentUser.setText("No account yet"));
    }

    private void requestTimelineRefresh() {
        FxAsyncUtils.doOnFxThread(timelineButton, btn -> btn.setDisable(true));
        CompletableFuture.runAsync(this.timelineManager::refreshTweets)
                .thenRunAsync(() -> FxAsyncUtils.doOnFxThread(timelineButton, btn -> btn.setDisable(false)));
    }

    private void openLoginWindow() {
        log.info("User requested login.");
        final Pane loginPane = this.easyFxml
                .loadNode(LOGIN_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        StageUtils.stageOf("Login", loginPane)
                .thenCompose(StageUtils::scheduleDisplaying)
                .thenAccept(stage -> this.stageManager.registerSingle(Views.LOGIN_VIEW, stage));
    }

    private void openTweetWindow() {
        log.info("Opening new tweet stage...");
        final Pane tweetPane = this.easyFxml
                .loadNode(TWEET_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        StageUtils.stageOf("Tweet", tweetPane)
                .thenCompose(StageUtils::scheduleDisplaying)
                .thenAccept(stage -> this.stageManager.registerSingle(TWEET_VIEW, stage))
                .thenRun(() -> log.info("New tweet stage opened !"));
    }
}
