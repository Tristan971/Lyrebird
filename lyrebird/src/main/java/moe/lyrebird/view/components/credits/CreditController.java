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

package moe.lyrebird.view.components.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import moe.lyrebird.model.credits.objects.CreditedWork;
import moe.lyrebird.view.screens.Screen;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

/**
 * This component is the one managing a single credit disclaimer unit in the credits list view.
 *
 * @see Screen#CREDITS_VIEW
 */
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class CreditController implements ComponentCellFxmlController<CreditedWork> {

    @FXML
    private Label title;

    @FXML
    private Hyperlink author;

    @FXML
    private Hyperlink licensor;

    @FXML
    private Hyperlink license;

    private final Property<CreditedWork> creditedWork;
    private final BrowserSupport browserSupport;

    @Autowired
    public CreditController(final BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
        this.creditedWork = new SimpleObjectProperty<>(null);
    }

    @Override
    public void updateWithValue(final CreditedWork newValue) {
        creditedWork.setValue(newValue);
    }

    @Override
    public void initialize() {
        creditedWork.addListener((o, prev, cur) -> {
            if (cur != null) {
                Platform.runLater(() -> {
                    title.setText(cur.getTitle());
                    author.setText(cur.getAuthor().getName());
                    author.setOnAction(e -> browserSupport.openUrl(cur.getAuthor().getUrl()));
                    licensor.setText(cur.getLicensor().getName());
                    licensor.setOnAction(e -> browserSupport.openUrl(cur.getLicensor().getUrl()));
                    license.setText(cur.getLicense().getName());
                    license.setOnAction(e -> browserSupport.openUrl(cur.getLicense().getUrl()));
                });
            }
        });
    }

}
