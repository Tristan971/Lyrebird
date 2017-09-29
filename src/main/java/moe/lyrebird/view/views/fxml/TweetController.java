package moe.lyrebird.view.views.fxml;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import moe.lyrebird.lang.javafx.EventUtils;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.tristan.easyfxml.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import static io.vavr.API.*;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.paint.Color.*;

@Component
public class TweetController implements FxmlController {

    private final TwitterHandler twitterHandler;
    @FXML
    private Button sendButton;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;

    @Autowired
    public TweetController(final TwitterHandler twitterHandler) {
        this.twitterHandler = twitterHandler;
    }

    @Override
    public void initialize() {
        tweetTextArea.addEventHandler(KEY_RELEASED, keyEventHandler());
        sendButton.addEventHandler(MOUSE_RELEASED, e -> sendTweet(this.tweetTextArea.getText()));

        tweetTextArea.fireEvent(EventUtils.dummyKeyEvent(KEY_RELEASED));
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
        try {
            this.twitterHandler.getTwitter().updateStatus(text);
        } catch (final IllegalStateException e) {
            ExceptionPane.displayExceptionPane(
                    "Cannot post tweet",
                    "You are not connected so we are unable to send this tweet.",
                    e
            );
        } catch (final TwitterException e) {
            ExceptionPane.displayExceptionPane(
                    "Twitter Error",
                    "We could not send your tweet. The error is :",
                    e
            );
        }
    }

}
