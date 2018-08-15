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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import moe.lyrebird.api.client.LyrebirdServerClient;
import moe.lyrebird.api.model.LyrebirdVersion;
import moe.lyrebird.model.interrupts.CleanupOperation;
import moe.lyrebird.model.interrupts.CleanupService;
import moe.lyrebird.model.notifications.Notification;
import moe.lyrebird.model.notifications.NotificationService;
import moe.lyrebird.model.notifications.NotificationSystemType;
import moe.lyrebird.model.update.selfupdate.SelfupdateService;

/**
 * The update service takes care of all things related to update check and installation
 */
@Component
public class UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateService.class);

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final Pattern BUILD_VERSION_PATTERN = Pattern.compile("\\.");

    private final LyrebirdServerClient apiClient;
    private final NotificationService notificationService;
    private final SelfupdateService selfupdateService;
    private final CleanupService cleanupService;

    private final String currentVersion;
    private final int currentBuildVersion;

    private final BooleanProperty isUpdateAvailable = new SimpleBooleanProperty(false);
    private final Property<LyrebirdVersion> latestVersion = new SimpleObjectProperty<>(null);

    public UpdateService(
            final LyrebirdServerClient apiClient,
            final NotificationService notificationService,
            final SelfupdateService selfupdateService,
            final Environment environment,
            final CleanupService cleanupService
    ) {
        this.apiClient = apiClient;
        this.notificationService = notificationService;
        this.selfupdateService = selfupdateService;
        this.currentVersion = environment.getRequiredProperty("app.version");
        this.cleanupService = cleanupService;
        this.currentBuildVersion = getCurrentBuildVersion();
        this.isUpdateAvailable.addListener((o, prev, cur) -> handleUpdateStatus(prev, cur));
        startPolling();
    }

    /**
     * @return the asynchronously fetched last version according to the API
     */
    public CompletableFuture<LyrebirdVersion> getLatestVersion() {
        return CompletableFuture.supplyAsync(() -> {
            if (latestVersion.getValue() == null) {
                poll();
            }
            return latestVersion.getValue();
        });
    }

    public BooleanProperty isUpdateAvailableProperty() {
        return isUpdateAvailable;
    }

    /**
     * @return the asynchronously fetched changenotes for the latest version
     */
    public CompletableFuture<String> getLatestChangeNotes() {
        return CompletableFuture.supplyAsync(
                () -> apiClient.getChangeNotes(latestVersion.getValue().getBuildVersion())
        );
    }

    /**
     * Starts self-updating to the latest version available
     */
    public void selfupdate() {
        getLatestVersion().thenAcceptAsync(selfupdateService::selfupdate);
    }

    /**
     * @return whether the current platform supports self-updating
     */
    public static boolean selfupdateCompatible() {
        return SelfupdateService.selfupdateCompatible();
    }

    /**
     * Starts the scheduled check for updates
     */
    private void startPolling() {
        EXECUTOR.scheduleAtFixedRate(
                this::poll,
                10L,
                5L * 60L,
                TimeUnit.SECONDS
        );
        cleanupService.registerCleanupOperation(new CleanupOperation(
                "Stop update service",
                EXECUTOR::shutdownNow
        ));
    }

    /**
     * Fetches the latest version and sets it in {@link #latestVersion}. If this version has a newer {@link
     * LyrebirdVersion#buildVersion} than the current one, set {@link #isUpdateAvailable} to true
     */
    private void poll() {
        try {
            LOG.debug("Checking for updates...");
            final LyrebirdVersion latestVersionServer = apiClient.getLatestVersion();
            LOG.debug("Latest version : {}", latestVersionServer.getVersion());
            this.latestVersion.setValue(latestVersionServer);
            isUpdateAvailable.setValue(latestVersionServer.getBuildVersion() > currentBuildVersion);
        } catch (final Exception e) {
            LOG.error("Could not check for updates !", e);
        }
    }

    /**
     * @return the buildVersion of the currently running application
     */
    private int getCurrentBuildVersion() {
        final String formatted = BUILD_VERSION_PATTERN.matcher(currentVersion).replaceAll("");
        return Integer.parseInt(formatted);
    }

    /**
     * Handles changes to {@link #isUpdateAvailable}. If it becomes true, we notify the user via the OS of the
     * availability of the update.
     *
     * @param prev previous status
     * @param cur  new status
     */
    private void handleUpdateStatus(final boolean prev, final boolean cur) {
        if (cur && !prev) {
            LOG.debug("An update was detected. Notifying the user.");
            notificationService.sendNotification(
                    new Notification(
                            "Update available!",
                            "An update is available for Lyrebird, grab it :-)"
                    ),
                    NotificationSystemType.SYSTEM
            );
        } else if (!cur) {
            LOG.debug("No update available.");
        }
    }

}
