package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.api.FxmlStylesheet;
import moe.tristan.easyfxml.spring.application.FxUiManager;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.screens.Screens;

import javafx.stage.Stage;

import java.util.Optional;

/**
 * The {@link LyrebirdUiManager} is responsible for bootstraping the GUI of the application correctly.
 */
@Slf4j
@Component
public class LyrebirdUiManager extends FxUiManager {

    private final Environment environment;

    @Autowired
    public LyrebirdUiManager(final EasyFxml easyFxml, final Environment environment) {
        super(easyFxml);
        this.environment = environment;
    }

    @Override
    protected String title() {
        return String.format(
                "%s [%s]",
                "Lyrebird",
                environment.getProperty("app.version")
        );
    }

    @Override
    protected void onStageCreated(Stage mainStage) {
        mainStage.setMinHeight(environment.getRequiredProperty("mainStage.minHeigth", Integer.class));
        mainStage.setMinWidth(environment.getRequiredProperty("mainStage.minWidth", Integer.class));
    }

    @Override
    protected FxmlNode mainComponent() {
        return Screens.ROOT_VIEW;
    }

    @Override
    protected Optional<FxmlStylesheet> getStylesheet() {
        return Optional.of(FxmlStylesheet.DEFAULT_JAVAFX_STYLE);
    }

}
