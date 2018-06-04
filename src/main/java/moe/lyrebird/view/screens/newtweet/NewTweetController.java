package moe.lyrebird.view.screens.newtweet;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.util.Stages;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.view.screens.Screens;
import moe.lyrebird.view.util.Events;
import twitter4j.Twitter;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;
import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

@Component
public class NewTweetController implements FxmlController {

    @FXML
    private Button sendButton;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;

    private final SessionManager sessionManager;
    private final StageManager stageManager;

    public NewTweetController(final SessionManager sessionManager, final StageManager stageManager) {
        this.sessionManager = sessionManager;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize() {
        tweetTextArea.addEventHandler(KEY_RELEASED, keyEventHandler());
        sendButton.addEventHandler(MOUSE_RELEASED, e -> sendTweet(this.tweetTextArea.getText()));

        tweetTextArea.fireEvent(Events.dummyKeyEvent(KEY_RELEASED));
    }

    private Tuple2<Color, Integer> validateCharactersLeft(final String currentText) {
        final Integer len = currentText.length();

        final Color color = Match(len).of(
                Case($(tweetLen -> tweetLen < 130), GREEN),
                Case($(tweetLen -> tweetLen > 130 && tweetLen < 140), YELLOW),
                Case($(tweetLen -> tweetLen > 140), RED),
                Case($(tweetLen -> tweetLen == 140), BLUE)
        );

        return Tuple.of(color, len);
    }

    private EventHandler<KeyEvent> keyEventHandler() {
        return keyEvent -> {
            final Tuple2<Color, Integer> checkUpResult = validateCharactersLeft(tweetTextArea.getText());

            Platform.runLater(() -> {
                charactersLeft.setText(String.valueOf(checkUpResult._2));
                charactersLeft.setTextFill(checkUpResult._1);
            });
        };
    }

    private void sendTweet(final String text) {
        final Try<Twitter> currentTwitter = sessionManager.getCurrentTwitter();

        CompletableFuture<Void> tweetRequest = CompletableFuture.runAsync(
                () -> currentTwitter.andThenTry(twitter -> twitter.updateStatus(text))
        );

        Stream.of(tweetTextArea, sendButton).forEach(ctr -> ctr.setDisable(true));

        tweetRequest.whenCompleteAsync((succ, err) -> {
            if (err != null) {
                displayExceptionPane(
                        "Could not send tweet!",
                        "There was an issue posting this tweet.",
                        err
                );
            } else {
                stageManager.getSingle(Screens.TWEET_VIEW).peek(Stages::scheduleHiding);
            }
        });
    }

}
