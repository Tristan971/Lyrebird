package moe.lyrebird.view.views;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Tristan on 06/02/2017.
 */
@UtilityClass
public class ErrorPane {
    public static Pane of(final String message, final Throwable throwable) {
        final Label messageLabel = new Label(message);
        final TextArea throwableDataLabel = new TextArea(
                "Message : \n" +
                        throwable.getMessage() +
                        "\nStackTrace:\n" +
                        Arrays.stream(throwable.getStackTrace())
                                .map(StackTraceElement::toString)
                                .collect(Collectors.joining("\n"))
        );
        AnchorPane.setLeftAnchor(messageLabel, 20.0);
        messageLabel.setStyle("-fx-font-weight: bold !important;");
        AnchorPane.setTopAnchor(throwableDataLabel, 20.0);
        AnchorPane.setBottomAnchor(throwableDataLabel, 20.0);
        AnchorPane.setLeftAnchor(throwableDataLabel, 20.0);
        AnchorPane.setRightAnchor(throwableDataLabel, 20.0);
    
        return new AnchorPane(messageLabel, throwableDataLabel);
    }
    
    public static void displayErrorPaneOf(final String message, final Throwable throwable) {
        final Pane errorPane = of(message, throwable);
        final Scene scene = new Scene(errorPane);
        final Stage errorStage = new Stage(StageStyle.DECORATED);
        errorStage.setScene(scene);
        errorStage.setTitle(message);
        Platform.runLater(errorStage::show);
    }
}
