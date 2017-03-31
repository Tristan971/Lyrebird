package moe.lyrebird.view.views;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.experimental.UtilityClass;
import moe.lyrebird.view.util.FXDOMUtils;
import moe.lyrebird.view.util.StageUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Tristan on 06/02/2017.
 */
@UtilityClass
public class ErrorPane {
    public static Pane of(final String message, final Throwable throwable) {
        final Label messageLabel = new Label(message);
        final TextArea throwableDataLabel = new TextArea(formatErrorMessage(throwable));
    
        messageLabel.getStyleClass().add("errorPane");
        FXDOMUtils.centerNode(throwableDataLabel, 20.0);
        return new AnchorPane(messageLabel, throwableDataLabel);
    }
    
    public static String formatErrorMessage(Throwable t) {
        return "Message : \n" +
                t.getMessage() +
                "\nStackTrace:\n" +
                Arrays.stream(t.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"));
    }
    
    public static void displayErrorPaneOf(final String message, final Throwable throwable) {
        StageUtils.stageOf(message, of(message, throwable), false);
    }
}
