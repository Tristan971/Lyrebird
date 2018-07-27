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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.User;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static moe.lyrebird.view.components.FxComponent.DIRECT_MESSAGE_CONVERSATION;

@Lazy
@Component
public class DirectMessagesController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessagesController.class);

    @FXML
    private TabPane conversationsTabPane;

    private final EasyFxml easyFxml;
    private final DirectMessages directMessages;

    private final Set<User> loadedPals = new HashSet<>();
    private final ObservableList<Tab> conversationsManaged;

    @Autowired
    public DirectMessagesController(final EasyFxml easyFxml, final DirectMessages directMessages) {
        this.easyFxml = easyFxml;
        this.directMessages = directMessages;
        this.conversationsManaged = FXCollections.observableArrayList();
        listenToNewConversations();
    }

    @Override
    public void initialize() {
        LOG.info("Displaying direct messages.");
        Bindings.bindContent(conversationsTabPane.getTabs(), conversationsManaged);
    }

    private void listenToNewConversations() {
        directMessages.directMessages().keySet().forEach(this::createTabForPal);
        JavaFxObservable.additionsOf(directMessages.directMessages())
                        .map(Map.Entry::getKey)
                        .forEach(this::createTabForPal);
    }

    private void createTabForPal(final User user) {
        if (loadedPals.contains(user)) return;
        LOG.debug("Creating a conversation tab for conversation with {}", user.getScreenName());
        easyFxml.loadNode(DIRECT_MESSAGE_CONVERSATION, Pane.class, DMConversationController.class)
                .afterControllerLoaded(dmc -> dmc.setPal(user))
                .getNode()
                .recover(ExceptionHandler::fromThrowable)
                .map(conversation -> new Tab(user.getName(), conversation))
                .onSuccess(tab -> Platform.runLater(() -> {
                    LOG.debug("Adding [{}] as tab for user {}", tab.getText(), user.getScreenName());
                    this.conversationsManaged.add(tab);
                }));
        loadedPals.add(user);
    }

}
