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

package moe.lyrebird.view.components.cells;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentListCell;
import twitter4a.DirectMessageEvent;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * This is the class managing the cell of a direct message from the point of view of its embedding {@link ListView}.
 *
 * @see ComponentListCell
 */
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class DirectMessageListCell extends ListCell<DirectMessageEvent> {

    @Override
    protected void updateItem(final DirectMessageEvent item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
        } else {
            setText(item.getText());
        }
    }

}
