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

package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

@Component
public class DirectMessagesController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessagesController.class);

    @FXML
    private TabPane conversationsPane;

    private final EasyFxml easyFxml;
    private final DirectMessages directMessages;

    @Autowired
    public DirectMessagesController(final EasyFxml easyFxml, final DirectMessages directMessages) {
        this.easyFxml = easyFxml;
        this.directMessages = directMessages;
    }

    @Override
    public void initialize() {
        LOG.info("Loading direct messages!");
        directMessages.loadDirectMessages();
        displayMessages();
    }

    private void displayMessages() {
    }

}
