package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsyncUtils;
import org.springframework.stereotype.Component;

import static moe.lyrebird.view.views.Views.CONTROL_BAR;
import static moe.lyrebird.view.views.Views.TIMELINE_VIEW;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
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
        log.info("Initializing control bar...");
        final Pane controlBarPane = this.easyFxml
                .loadNode(CONTROL_BAR)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());
        log.info("Initialized control bar !");
        log.info("Displaying control bar...");
        FxAsyncUtils.doOnFxThread(contentPane, root -> root.setLeft(controlBarPane));
    }

    private void loadTimeline() {
        log.info("Loading timeline view.");
        final Pane timelinePane = this.easyFxml
                .loadNode(TIMELINE_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        this.contentPane.setCenter(timelinePane);
        log.info("Loaded timeline view.");
    }
}
