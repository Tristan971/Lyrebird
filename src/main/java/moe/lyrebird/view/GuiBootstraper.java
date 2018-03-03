package moe.lyrebird.view;

import io.vavr.control.Try;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import org.springframework.core.env.Environment;

import java.util.ResourceBundle;

/**
 * The {@link GuiBootstraper} is responsible for bootstraping the
 * GUI of the application correctly.
 */
@Slf4j
@RequiredArgsConstructor
public class GuiBootstraper {

    private final Environment environment;
    private final ResourceBundle resourceBundle;
    private final EasyFxml easyFxml;

    @Getter
    private Stage mainStage;

    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(true);
        this.mainStage = primaryStage;
        primaryStage.setScene(this.getRootScene());
        primaryStage.setTitle(this.getMainStageTitle());
        primaryStage.show();
    }

    private Scene getRootScene() {
        final Try<Pane> rootPane = easyFxml
                .loadNode(Views.ROOT_VIEW)
                .recover(err -> new ExceptionHandler(err).asPane());
        return new Scene(rootPane.get());
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
