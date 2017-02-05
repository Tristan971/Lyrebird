package moe.lyrebird.view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
public final class GUIManager extends Application {
    
    public static void runGui(final String[] args) {
        launch(args);
    }
    
    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.show();
    }
}
