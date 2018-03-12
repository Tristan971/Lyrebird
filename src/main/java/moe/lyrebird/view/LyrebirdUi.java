package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.spring.application.FxUiManager;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Views;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The {@link LyrebirdUi} is responsible for bootstraping the GUI of the application correctly.
 */
@Slf4j
@Component
public class LyrebirdUi extends FxUiManager {

    private final String appVersion;

    @Autowired
    public LyrebirdUi(
            @Value("${app.version}") final String appVersion,
            final EasyFxml easyFxml
    ) {
        super(easyFxml);
        this.appVersion = appVersion;
    }

    @Override
    public void startGui(final Stage primaryStage) {
        Platform.setImplicitExit(true);
        getScene(Views.ROOT_VIEW).andThen(scene -> {
            primaryStage.setScene(scene);
            primaryStage.setTitle(this.getMainStageTitle());
            //Stages.setStylesheet(primaryStage, Styles.LYREBIRD);
            primaryStage.show();
        });
    }

    private String getMainStageTitle() {
        return String.format(
                "%s [%s]",
                "Lyrebird",
                appVersion
        );
    }

}
