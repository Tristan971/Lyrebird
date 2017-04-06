package moe.lyrebird.view;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
public class GUIManager {
    private final Environment environment;
    private final ResourceBundle resourceBundle;
    
    @Getter
    private final ViewLoader viewLoader;
    @Getter
    private final Map<Class<? extends Controller>, Stage> stages = new HashMap<>();
    @Getter
    private Stage mainStage;
    
    @Autowired
    public GUIManager(final Environment environment, final ViewLoader viewLoader, final ResourceBundle resourceBundle) {
        this.environment = environment;
        this.viewLoader = viewLoader;
        this.resourceBundle = resourceBundle;
    }
    
    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(true);
        this.mainStage = primaryStage;
        primaryStage.setScene(this.viewLoader.getRootScene());
        primaryStage.setTitle(this.getMainStageTitle());
        primaryStage.show();
    }
    
    private String getMainStageTitle() {
        return String.format(
                "%s [%s]",
                this.resourceBundle.getString("mainWindow.title"),
                this.environment.getProperty("app.version")
        );
    }
    
    public void registerStage(final Class<? extends Controller> controllerClass, final Stage stage) {
        this.stages.put(controllerClass, stage);
        final String className = controllerClass.getSimpleName();
        log.info("Registered {} stage ({})", className, stage.toString());
    }
    
    /**
     * Must be called (only once) from a non-JavaFX thread to enable AWT functionnality.
     * Thus the place to do it is {@link moe.lyrebird.Lyrebird#main(String...)}
     */
    public static void enableAWT() {
        java.awt.Toolkit.getDefaultToolkit();
    }
}
