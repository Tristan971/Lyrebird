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

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

import moe.lyrebird.api.model.LyrebirdVersion;
import moe.lyrebird.model.update.UpdateService;
import moe.lyrebird.view.screens.Screen;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;

/**
 * This controller is responsible for managing the {@link Screen#UPDATE_VIEW} screen.
 */
@Component
public class UpdateScreenController implements FxmlController {

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Label latestVersionLabel;

    @FXML
    private WebView changeNotesWebView;

    @FXML
    private Button updateButton;

    @FXML
    private Button openInBrowserUrl;

    private final Environment environment;
    private final UpdateService updateService;
    private final BrowserSupport browserSupport;

    public UpdateScreenController(
            final Environment environment,
            final UpdateService updateService,
            final BrowserSupport browserSupport
    ) {
        this.environment = environment;
        this.updateService = updateService;
        this.browserSupport = browserSupport;
    }

    @Override
    public void initialize() {
        updateService.getLatestVersion()
                     .thenAcceptAsync(this::displayVersion, Platform::runLater);

        final boolean canSelfupdate = UpdateService.selfupdateCompatible();
        updateButton.setVisible(canSelfupdate);
        updateButton.setManaged(canSelfupdate);
        updateButton.setOnAction(e -> {
            this.updateButton.setDisable(true);
            this.updateService.selfupdate();
        });
    }

    /**
     * Displays the current version and the one detected as latest from the API.
     *
     * @param latestVersion The latest version fetched from the API.
     */
    private void displayVersion(final LyrebirdVersion latestVersion) {
        this.currentVersionLabel.setText(environment.getRequiredProperty("app.version"));
        this.latestVersionLabel.setText(latestVersion.getVersion());

        this.openInBrowserUrl.setOnAction(e -> browserSupport.openUrl(latestVersion.getReleaseUrl()));

        updateService.getLatestChangeNotes()
                     .thenAcceptAsync(this.changeNotesWebView.getEngine()::loadContent, Platform::runLater);
    }

}
