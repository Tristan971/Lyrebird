package moe.lyrebird.view.screens.root;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import static moe.lyrebird.view.components.Components.CONTROL_BAR;
import static moe.lyrebird.view.components.Components.TIMELINE;

/**
 * The RootViewController manages the location of content on the root view scene.
 */
@Component
public class RootViewController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(RootViewController.class);

    @FXML
    private BorderPane contentPane;

    private final EasyFxml easyFxml;

    public RootViewController(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    @Override
    public void initialize() {
        loadControlBar();
        loadTimeline();
    }

    private void loadControlBar() {
        LOG.debug("Initializing control bar...");
        final Pane controlBarPane = this.easyFxml
                .loadNode(CONTROL_BAR)
                .getNode()
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());
        LOG.debug("Initialized control bar !");
        FxAsync.doOnFxThread(contentPane, root -> root.setLeft(controlBarPane));
    }

    private void loadTimeline() {
        LOG.info("Loading timeline view.");
        final Pane timelinePane = this.easyFxml
                .loadNode(TIMELINE)
                .getNode()
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        this.contentPane.setCenter(timelinePane);
        LOG.info("Loaded timeline view.");
    }
}
