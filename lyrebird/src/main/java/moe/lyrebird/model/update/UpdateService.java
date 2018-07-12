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

package moe.lyrebird.model.update;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);

    private final ScheduledExecutorService updateExecutor;
    private final MarkdownRenderingService markdownRenderingService;
    private final LyrebirdServerClient client;

    private final String currentVersion;
    private final int currentBuildVersion;
    private final Property<LyrebirdVersion> latestVersion = new SimpleObjectProperty<>(null);

    public UpdateService(
            @Qualifier("updateExecutor") final ScheduledExecutorService updateExecutor,
            final MarkdownRenderingService markdownRenderingService,
            final LyrebirdServerClient client,
            final Environment environment
    ) {
        this.updateExecutor = updateExecutor;
        this.markdownRenderingService = markdownRenderingService;
        this.client = client;
        this.currentVersion = environment.getRequiredProperty("app.version");
        this.currentBuildVersion = getCurrentBuildVersion();
        startPolling();
    }

    public CompletableFuture<LyrebirdVersion> getLatestVersion() {
        return CompletableFuture.supplyAsync(() -> {
            if (latestVersion.getValue() == null) {
                poll();
            }
            return latestVersion.getValue();
        }, updateExecutor);
    }

    public CompletableFuture<Boolean> isUpdateAvailable() {
        return getLatestVersion().thenApplyAsync(
                latestVersion -> {
                    final boolean updateAvailable = latestVersion.getBuildVersion() > currentBuildVersion;
                    LOG.info(updateAvailable ?
                             "An update is available! [current : {}, latest : {}]" :
                             "Already using latest version!",

                             latestVersion.getVersion()
                    );
                    return updateAvailable;
                },
                updateExecutor
        );
    }

    public CompletableFuture<String> getLatestChangeNotes() {
        return CompletableFuture.supplyAsync(
                () -> client.getChangeNotes(latestVersion.getValue().getBuildVersion()),
                updateExecutor
        ).thenApplyAsync(markdownRenderingService::render, updateExecutor);
    }

    private void startPolling() {
        updateExecutor.scheduleAtFixedRate(
                this::poll,
                10,
                5 * 60,
                TimeUnit.SECONDS
        );
    }

    private void poll() {
        try {
            LOG.debug("Checking for updates...");
            latestVersion.setValue(client.getLatestVersion());
        } catch (Exception e) {
            LOG.error("Could not check for updates !", e);
        }
    }

    private int getCurrentBuildVersion() {
        final String formatted = currentVersion.replaceAll("\\.", "");
        return Integer.parseInt(formatted);
    }

}
