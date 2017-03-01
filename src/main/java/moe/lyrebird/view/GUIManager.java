package moe.lyrebird.view;

import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
public class GUIManager {
    
    private final Environment environment;
    private final ViewLoader viewLoader;
    
    @Autowired
    public GUIManager(final Environment environment, final ViewLoader viewLoader) {
        this.environment = environment;
        this.viewLoader = viewLoader;
    }
    
    public void startGui(final Stage primaryStage) {
        primaryStage.setScene(this.viewLoader.loadScene(Views.ROOT_VIEW));
    
        primaryStage.setTitle(
                String.format(
                        "Lyrebird Alpha [%s]",
                        this.environment.getProperty("app.version")
                )
        );
        primaryStage.show();
    }
    
    private static void startAWT() {
        java.awt.Toolkit.getDefaultToolkit();
    }
}
