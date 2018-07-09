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

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.lyrebird.view.components.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import static moe.lyrebird.view.components.Components.CONTROL_BAR;

/**
 * The RootViewController manages the location of content on the root view scene.
 */
@Component
public class RootScreenController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(RootScreenController.class);

    @FXML
    private BorderPane contentPane;

    private final EasyFxml easyFxml;

    public RootScreenController(final EasyFxml easyFxml) {
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
