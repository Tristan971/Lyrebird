package moe.lyrebird.view.screens.newtweet;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.twitter.services.NewTweetService;
import moe.lyrebird.view.screens.Screens;
import twitter4j.Status;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

@Component
public class NewTweetController implements FxmlController {

    @FXML
    private Button sendButton;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;


    private final StageManager stageManager;
    private final NewTweetService newTweetService;

    public NewTweetController(final StageManager stageManager, final NewTweetService newTweetService) {
        this.stageManager = stageManager;
        this.newTweetService = newTweetService;
    }

    @Override
    public void initialize() {
        enableTweetLengthCheck();
        sendButton.addEventHandler(MOUSE_RELEASED, e -> sendTweet(this.tweetTextArea.getText()));
    }

    private void enableTweetLengthCheck() {
        tweetTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            final Color color = Match(newValue.length()).of(
                    Case($(tweetLen -> tweetLen < 250), GREEN),
                    Case($(tweetLen -> tweetLen >= 250 && tweetLen < 280), ORANGE),
                    Case($(tweetLen -> tweetLen > 280), RED),
                    Case($(tweetLen -> tweetLen == 280), BLUE)
            );
            Platform.runLater(() -> {
                charactersLeft.setText(Integer.toString(newValue.length()));
                charactersLeft.setTextFill(color);
            });
        });
    }

    private void sendTweet(final String text) {
        final CompletionStage<Status> tweetRequest = newTweetService.sendNewTweet(text, Collections.emptyList());

        Stream.of(tweetTextArea, sendButton).forEach(ctr -> ctr.setDisable(true));

        tweetRequest.whenCompleteAsync((succ, err) -> {
            if (err != null) {
                displayExceptionPane(
                        "Could not send tweet!",
                        "There was an issue posting this tweet.",
                        err
                );
            } else {
                stageManager.getSingle(Screens.NEW_TWEET_VIEW).peek(Stages::scheduleHiding);
            }
        });
    }

}
