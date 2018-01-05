package moe.lyrebird.view.views.fxml;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.concurrent.Future;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import moe.lyrebird.lang.javafx.EventUtils;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Stages;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import static io.vavr.API.*;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.paint.Color.*;

@Component
@RequiredArgsConstructor
public class TweetController implements FxmlController {

    @FXML
    private Button sendButton;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;

    private final TwitterHandler twitterHandler;
    private final StageManager stageManager;

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
        final Future<Status> updateResult = Future(
                () -> this.twitterHandler.getTwitter().updateStatus(text)
        );

        Stream(tweetTextArea, sendButton).forEach(ctr -> ctr.setDisable(true));

        updateResult.onComplete(res -> {
            res.onFailure(err ->
                    Stages.stageOf(
                            "Couldn't send tweet !",
                            new ExceptionHandler(err).asPane()
                    ).thenAccept(Stages::scheduleDisplaying));
            res.onSuccess(status ->
                    stageManager.getSingle(Views.TWEET_VIEW).peek(Stages::scheduleHiding)
            );
        });
    }

}
