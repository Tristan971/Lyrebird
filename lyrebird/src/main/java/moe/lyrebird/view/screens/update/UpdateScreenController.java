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

package moe.lyrebird.view.screens.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

@Component
public class UpdateScreenController implements FxmlController {

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Label latestVersionLabel;

    @FXML
    private WebView changeNotesWebView;

    private final LyrebirdServerClient client;
    private final Environment environment;

    @Autowired
    public UpdateScreenController(
            final LyrebirdServerClient client,
            final Environment environment
    ) {
        this.client = client;
        this.environment = environment;
    }

    @Override
    public void initialize() {
        final LyrebirdVersion latestVersion = client.getLatestVersion();
        this.currentVersionLabel.setText(environment.getRequiredProperty("app.version"));
        this.latestVersionLabel.setText(latestVersion.getVersion());
        this.changeNotesWebView.getEngine().loadContent(latestVersion.getChangenotes());
    }

}
