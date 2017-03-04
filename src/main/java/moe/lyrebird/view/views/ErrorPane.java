package moe.lyrebird.view.views;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

/**
 * Created by Tristan on 06/02/2017.
 */
@UtilityClass
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
    
    public static void displayErrorPaneOf(final String message, final Throwable throwable) {
        final Pane errorPane = of(message, throwable);
        final Scene scene = new Scene(errorPane);
        final Stage errorStage = new Stage(StageStyle.DECORATED);
        errorStage.setScene(scene);
        errorStage.setTitle(message);
        Platform.runLater(errorStage::show);
    }
}
