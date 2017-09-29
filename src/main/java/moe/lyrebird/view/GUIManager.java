package moe.lyrebird.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.model.views.ViewsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.ResourceBundle;

import static moe.tristan.easyfxml.model.exception.ExceptionPaneBehavior.DIALOG;

/**
 * The {@link GUIManager} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
public class GUIManager {
    private final Environment environment;
    private final ResourceBundle resourceBundle;
    
    @Getter
    private final ViewsLoader viewsLoader;
    @Getter
    private Stage mainStage;
    
    @Autowired
    public GUIManager(final Environment environment, final ViewsLoader viewsLoader, final ResourceBundle resourceBundle) {
        this.environment = environment;
        this.viewsLoader = viewsLoader;
        this.resourceBundle = resourceBundle;
    }
    
    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(true);
        this.mainStage = primaryStage;
        primaryStage.setScene(this.getRootScene(viewsLoader));
        primaryStage.setTitle(this.getMainStageTitle());
        primaryStage.show();
    }

    private Scene getRootScene(ViewsLoader viewsLoader) {
        return new Scene(viewsLoader.loadPaneForView(Views.ROOT_VIEW, DIALOG));
    }

    private String getMainStageTitle() {
        return String.format(
                "%s [%s]",
                this.resourceBundle.getString("mainWindow.title"),
                this.environment.getProperty("app.version")
        );
    }
    
    /**
     * Must be called (only once) from a non-JavaFX thread to enable AWT functionnality.
     * Thus the place to do it is {@link moe.lyrebird.Lyrebird#main(String...)}
     */
    public static void enableAWT() {
        java.awt.Toolkit.getDefaultToolkit();
    }

}
