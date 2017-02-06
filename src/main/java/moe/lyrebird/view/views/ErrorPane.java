package moe.lyrebird.view.views;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import java.util.Arrays;

/**
 * Created by Tristan on 06/02/2017.
 */
public class ErrorPane {
    public static Pane of(final String message, final Throwable throwable) {
        final Label messageLabel = new Label(message);
        final TextArea throwableDataLabel = new TextArea(
                throwable.getMessage() +
                        '\n' +
                        Arrays.toString(throwable.getStackTrace())
        );
        
        throwableDataLabel.setLayoutY(20.0);
        return new Pane(messageLabel, throwableDataLabel);
    }
}
