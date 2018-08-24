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

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentListViewFxmlController;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import moe.lyrebird.model.twitter.services.NewDirectMessageService;
import moe.lyrebird.view.components.cells.DirectMessageListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.DirectMessage;
import twitter4j.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class DMConversationController extends ComponentListViewFxmlController<DirectMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(DMConversationController.class);

    @FXML
    private TextArea messageContent;

    @FXML
    private Button sendButton;

    private final DirectMessages directMessages;
    private final NewDirectMessageService newDirectMessageService;

    private final Property<User> currentPal = new SimpleObjectProperty<>();

    public DMConversationController(
            final DirectMessages directMessages,
            final ApplicationContext applicationContext,
            final NewDirectMessageService newDirectMessageService
    ) {
        super(applicationContext, DirectMessageListCell.class);
        this.directMessages = directMessages;
        this.newDirectMessageService = newDirectMessageService;
    }

    @Override
    public void initialize() {
        super.initialize();
        LOG.debug("Schedule displaying of conversation once senderId has been received!");
        sendButton.setOnAction(e -> sendMessage());
    }

    public void setPal(final User pal) {
        LOG.debug("Messages for [{}] loaded!", pal.getScreenName());
        this.currentPal.setValue(pal);
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(directMessages.directMessages().get(pal)));
    }

    private void sendMessage() {
        LOG.debug("Sending direct message!");
        messageContent.setDisable(true);
        sendButton.setDisable(true);
        newDirectMessageService.sendMessage(
                currentPal.getValue(),
                messageContent.getText()
        ).whenCompleteAsync((res, err) -> {
            messageContent.clear();
            messageContent.setDisable(false);
            sendButton.setDisable(false);
        }, Platform::runLater);
    }

}
