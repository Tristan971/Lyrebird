package moe.lyrebird.view.screens.root;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import static moe.lyrebird.view.components.Components.CONTROL_BAR;
import static moe.lyrebird.view.components.Components.TIMELINE;

/**
 * The RootViewController manages the location of content on the root view scene.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RootViewController implements FxmlController {

    @FXML
    private BorderPane contentPane;

    private final EasyFxml easyFxml;

    @Override
    public void initialize() {
        loadControlBar();
        loadTimeline();
    }

    private void loadControlBar() {
        log.debug("Initializing control bar...");
        final Pane controlBarPane = this.easyFxml
                .loadNode(CONTROL_BAR)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());
        log.debug("Initialized control bar !");
        FxAsync.doOnFxThread(contentPane, root -> root.setLeft(controlBarPane));
    }

    private void loadTimeline() {
        log.info("Loading timeline view.");
        final Pane timelinePane = this.easyFxml
                .loadNode(TIMELINE)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        this.contentPane.setCenter(timelinePane);
        log.info("Loaded timeline view.");
    }
}
