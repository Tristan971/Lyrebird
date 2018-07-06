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

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import moe.lyrebird.view.components.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.DirectMessage;
import twitter4j.User;

import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class DirectMessagesController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessagesController.class);

    public TabPane conversationsPane;

    private final EasyFxml easyFxml;
    private final DirectMessages directMessages;

    private final Map<User, Tab> tabs;

    public DirectMessagesController(final EasyFxml easyFxml, final DirectMessages directMessages) {
        this.easyFxml = easyFxml;
        this.directMessages = directMessages;
        tabs = new HashMap<>();
    }

    @Override
    public void initialize() {
        directMessages.loadedConversations()
                      .addListener((MapChangeListener<? super User, ? super List<DirectMessage>>) this::handleConversationsChange);
        LOG.info("Loading direct messages!");
        directMessages.loadLatestDirectMessages();
    }

    private void handleConversationsChange(final Change<? extends User, ? extends List<DirectMessage>> change) {
        if (change.wasAdded()) {
            if (change.getValueRemoved() == null) {
                addConversation(change.getKey());
            }
        } else if (change.wasRemoved()) {
            if (change.getValueAdded() == null)
            removeConversation(change.getKey());
        }
    }

    private void addConversation(final User user) {
        LOG.debug("New conversation detected with {}", user.getScreenName());

        final FxmlLoadResult<Pane, DMConversationController> conversationLoad = easyFxml.loadNode(
                Components.DIRECT_MESSAGE_CONVERSATION,
                Pane.class,
                DMConversationController.class
        );

        final Pane conversationPane =
                conversationLoad.getNode()
                                .getOrElseGet(ExceptionHandler::fromThrowable);

        final DMConversationController conversationController =
                conversationLoad.getController()
                                .getOrElseThrow((Function<? super Throwable, RuntimeException>) RuntimeException::new);

        conversationController.setPal(user);

        final Tab tab = new Tab(user.getScreenName());
        tab.setContent(conversationPane);
        tabs.put(user, tab);
        conversationsPane.getTabs().add(tab);
    }

    private void removeConversation(final User user) {
        LOG.debug("Deleting conversation with {}", user.getScreenName());

        final Tab associatedTab = tabs.get(user);

        if (associatedTab == null) {
            LOG.warn("Tried to delete a DM tab with user {}, but it was not known to exist!!", user);
        } else {
            conversationsPane.getTabs().remove(associatedTab);
        }
    }

}
