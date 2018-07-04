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

package moe.lyrebird.view.screens.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentListViewFxmlController;
import moe.lyrebird.model.credits.CreditedWork;
import moe.lyrebird.model.credits.CreditsService;
import moe.lyrebird.view.components.cells.CreditsCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyListWrapper;

@Lazy
@Component
public class CreditsScreenController extends ComponentListViewFxmlController<CreditedWork> {

    private static final Logger LOG = LoggerFactory.getLogger(CreditsScreenController.class);

    private final CreditsService creditsService;

    @Autowired
    public CreditsScreenController(ApplicationContext context, CreditsService creditsService) {
        super(context, CreditsCell.class);
        this.creditsService = creditsService;
    }

    @Override
    public void initialize() {
        LOG.debug("Loading credits...");
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(creditsService.creditedWorks()));
    }

}
