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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

@Component
public class CreditsController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(CreditsController.class);

    private static final PathMatchingResourcePatternResolver PMPR = new PathMatchingResourcePatternResolver();

    public VBox creditsBox;

    @Override
    public void initialize() {
        LOG.debug("Loading credits...");
        loadCredits();
    }

    private void loadCredits() {
        findCreditsFiles().whenCompleteAsync(
                (credits, err) -> {
                    if (err != null) {
                        ExceptionHandler.displayExceptionPane(
                                "Could not load credits.",
                                "Something wrong happened when reading credits files.",
                                err
                        );
                    } else {
                        createAndLoadWebViews(credits);
                    }
                },
                Platform::runLater
        );
    }

    private void createAndLoadWebViews(final List<String> credits) {
        Platform.runLater(
                () -> credits.stream()
                             .map(credit -> {
                                 final WebView webView = new WebView();
                                 webView.getEngine().loadContent(credit, MimeTypeUtils.TEXT_HTML_VALUE);
                                 return webView;
                             })
                             .forEach(creditsBox.getChildren()::add)
        );
    }

    private CompletableFuture<List<String>> findCreditsFiles() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return Arrays.stream(PMPR.getResources("classpath:assets/credits/*"))
                                     .map(unchecked(Resource::getInputStream))
                                     .map(unchecked(is -> StreamUtils.copyToString(is, StandardCharsets.UTF_8)))
                                     .collect(Collectors.toList());
                    } catch (IOException e) {
                        LOG.error("Could not find credit files", e);
                        return Collections.emptyList();
                    }
                }
        );
    }
}
