package moe.lyrebird.view;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Views;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
public final class GUIManager {
    
    private final ViewLoader viewLoader;
    
    public GUIManager(final ViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }
    
    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(false);
        primaryStage.setScene(this.viewLoader.loadWindow(Views.ROOT_VIEW));
        primaryStage.show();
    }
}
