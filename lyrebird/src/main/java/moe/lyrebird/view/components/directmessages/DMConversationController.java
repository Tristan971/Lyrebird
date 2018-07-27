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
import moe.lyrebird.view.components.cells.DirectMessageListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.DirectMessageEvent;
import twitter4a.User;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class DMConversationController extends ComponentListViewFxmlController<DirectMessageEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DMConversationController.class);

    private final DirectMessages directMessages;

    private final Property<ObservableList<DirectMessageEvent>> loadedMessages = new SimpleObjectProperty<>();

    public DMConversationController(
            final DirectMessages directMessages,
            final ApplicationContext applicationContext
    ) {
        super(applicationContext, DirectMessageListCell.class);
        this.directMessages = directMessages;
    }

    @Override
    public void initialize() {
        super.initialize();
        LOG.debug("Schedule displaying of conversation once senderId has been received!");
        loadedMessages.addListener((observable, oldValue, newValue) -> listView.itemsProperty().set(newValue));
    }

    public void setPal(final User pal) {
        LOG.debug("Messages for [{}] loaded!", pal.getScreenName());
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(directMessages.directMessages().get(pal)));
    }

}
