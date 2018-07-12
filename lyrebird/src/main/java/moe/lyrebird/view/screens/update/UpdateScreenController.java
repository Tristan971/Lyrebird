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
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import moe.lyrebird.model.update.MarkdownRenderingService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

    private final Executor updateExecutor;
    private final MarkdownRenderingService markdownRenderingService;
    private final LyrebirdServerClient client;
    private final Environment environment;
    private final BrowserSupport browserSupport;

    @Autowired
    public UpdateScreenController(
            final Executor updateExecutor,
            final MarkdownRenderingService markdownRenderingService,
            final LyrebirdServerClient client,
            final Environment environment,
            final BrowserSupport browserSupport
    ) {
        this.updateExecutor = updateExecutor;
        this.markdownRenderingService = markdownRenderingService;
        this.client = client;
        this.environment = environment;
        this.browserSupport = browserSupport;
    }

    @Override
    public void initialize() {
        CompletableFuture.supplyAsync(client::getLatestVersion, updateExecutor)
                         .thenAcceptAsync(this::displayVersion, Platform::runLater);
    }

    private void displayVersion(final LyrebirdVersion latestVersion) {
        this.currentVersionLabel.setText(environment.getRequiredProperty("app.version"));
        this.latestVersionLabel.setText(latestVersion.getVersion());

        this.openInBrowserUrl.setOnAction(e -> browserSupport.openUrl(latestVersion.getReleaseUrl()));

        CompletableFuture.supplyAsync(() -> client.getChangeNotes(latestVersion.getBuildVersion()), updateExecutor)
                         .thenApplyAsync(markdownRenderingService::render, updateExecutor)
                         .thenAcceptAsync(this.changeNotesWebView.getEngine()::loadContent, Platform::runLater);
    }

}
