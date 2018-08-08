/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.view.screens.root;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import moe.lyrebird.view.components.FxComponent;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;

/**
 * The RootViewController manages the location of content on the root view scene.
 */
@Component
public class RootScreenController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(RootScreenController.class);

    @FXML
    private BorderPane contentPane;

    private final EasyFxml easyFxml;

    private final Map<FxComponent, Pane> cachedComponents = new ConcurrentHashMap<>();

    public RootScreenController(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    @Override
    public void initialize() {
        loadControlBar();
        loadNotificationPane();
    }

    /**
     * Loads up the {@link FxComponent#CONTROL_BAR} on the left side of the {@link BorderPane} which is the main
     * container
     * for the main view.
     */
    private void loadControlBar() {
        LOG.debug("Initializing control bar...");
        loadComponentAsync(FxComponent.CONTROL_BAR).thenAcceptAsync(
                this.contentPane::setLeft,
                Platform::runLater
        );
    }

    /**
     * Loads up the {@link FxComponent#NOTIFICATIONS_PANE} on the top side of the {@link BorderPane} which is the main
     * container for the main view.
     */
    private void loadNotificationPane() {
        LOG.debug("Initializing notification pane...");
        loadComponentAsync(FxComponent.NOTIFICATIONS_PANE).thenAcceptAsync(
                this.contentPane::setTop,
                Platform::runLater
        );
    }

    /**
     * Helper function to load a given {@link FxComponent} as center node for the {@link BorderPane} which is the main
     * container for the root view.
     *
     * @param fxComponent The fxComponent to load.
     */
    public void setContent(final FxComponent fxComponent) {
        loadComponentAsync(fxComponent).thenAcceptAsync(this.contentPane::setCenter, Platform::runLater);
    }

    private CompletableFuture<Pane> loadComponentAsync(final FxComponent fxComponent) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.info("Switching content of root pane to {}", fxComponent);
            final Pane loadedPane = cachedComponents.computeIfAbsent(fxComponent, this::loadComponentSynchronous);
            return Objects.requireNonNull(loadedPane);
        }, Platform::runLater);
    }

    private Pane loadComponentSynchronous(final FxComponent fxComponent) {
        return this.easyFxml
                .loadNode(fxComponent)
                .getNode()
                .getOrElseGet(ExceptionHandler::fromThrowable);
    }

}
