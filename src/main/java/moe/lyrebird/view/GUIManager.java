package moe.lyrebird.view;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Controller;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
public class GUIManager {
    private final Environment environment;
    
    @Getter
    private final ViewLoader viewLoader;
    @Getter
    private final Map<Class<? extends Controller>, Stage> stages = new HashMap<>();
    @Getter
    private Stage mainStage;
    
    @Autowired
    public GUIManager(final Environment environment, final ViewLoader viewLoader) {
        this.environment = environment;
        this.viewLoader = viewLoader;
    }
    
    public void startGui(final Stage primaryStage) {
        this.mainStage = primaryStage;
        primaryStage.setScene(this.viewLoader.loadScene(Views.ROOT_VIEW));
        primaryStage.setTitle(String.format("Lyrebird Alpha [%s]", this.environment.getProperty("app.version")));
        primaryStage.show();
    }
    
    public void registerStage(final Class<? extends Controller> controllerClass, final Stage stage) {
        this.stages.put(controllerClass, stage);
    }
    
    /**
     * Must be called (only once) from a non-JavaFX thread to enable AWT functionnality.
     * Thus the place to do it is {@link moe.lyrebird.Lyrebird#main(String...)}
     */
    public static void enableAWT() {
        java.awt.Toolkit.getDefaultToolkit();
    }
}
