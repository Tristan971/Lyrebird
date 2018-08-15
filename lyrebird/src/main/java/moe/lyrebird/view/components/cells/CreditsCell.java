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
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.components.listview.ComponentListCell;
import moe.lyrebird.model.credits.objects.CreditedWork;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListView;

import static moe.lyrebird.view.components.FxComponent.CREDIT;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * This is the class managing the cell of a credit disclaimer from the point of view of its embedding {@link ListView}.
 *
 * @see ComponentListCell
 */
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class CreditsCell extends ComponentListCell<CreditedWork> {

    private final BooleanProperty shouldDisplay;

    public CreditsCell(final EasyFxml easyFxml) {
        super(easyFxml, CREDIT);
        this.shouldDisplay = new SimpleBooleanProperty(false);
        this.cellNode.visibleProperty().bind(shouldDisplay);
    }

    @Override
    protected void updateItem(final CreditedWork item, final boolean empty) {
        super.updateItem(item, empty);
        this.shouldDisplay.setValue(item != null && !empty);
    }
}
