package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Stages;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Styles;
import moe.lyrebird.view.views.Views;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The {@link GuiBootstraper} is responsible for bootstraping the GUI of the application correctly.
 */
@Slf4j
@Component
public class GuiBootstraper {

    private final EasyFxml easyFxml;
    private final String appVersion;

    @Getter
    private Stage mainStage;

    @Autowired
    public GuiBootstraper(
            @Value("${app.version}") final String appVersion,
            final EasyFxml easyFxml
    ) {
        this.appVersion = appVersion;
        this.easyFxml = easyFxml;
    }

    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(true);
        this.mainStage = primaryStage;
        primaryStage.setScene(this.getRootScene());
        primaryStage.setTitle(this.getMainStageTitle());
        Stages.setStylesheet(mainStage, Styles.LYREBIRD);
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
                "Lyrebird",
                appVersion
        );
    }

}
