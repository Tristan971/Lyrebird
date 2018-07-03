package moe.lyrebird.view.screens.root;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.lyrebird.view.components.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import static moe.lyrebird.view.components.Components.CONTROL_BAR;

/**
 * The RootViewController manages the location of content on the root view scene.
 */
@Component
public class RootViewController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(RootViewController.class);

    public BorderPane contentPane;

    private final EasyFxml easyFxml;

    public RootViewController(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    @Override
    public void initialize() {
        loadControlBar();
    }

    public void setContent(final Components component) {
        LOG.info("Switching content of root pane to {}", component.name());
        final Pane contentNode = this.easyFxml
                .loadNode(component)
                .getNode()
                .getOrElseGet(ExceptionHandler::fromThrowable);

        this.contentPane.setCenter(contentNode);
        LOG.info("Set content of root pane to {}", component.name());
    }

    private void loadControlBar() {
        LOG.debug("Initializing control bar...");
        final Pane controlBarPane = this.easyFxml
                .loadNode(CONTROL_BAR)
                .getNode()
                .getOrElseGet(ExceptionHandler::fromThrowable);
        LOG.debug("Initialized control bar !");
        contentPane.setLeft(controlBarPane);
    }

}
