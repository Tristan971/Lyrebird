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

package moe.lyrebird.model.update.selfupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.vavr.control.Option;
import moe.lyrebird.api.model.LyrebirdVersion;
import moe.lyrebird.api.model.TargetPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * This service is in charge of the orchestration of the selfupdate process
 */
@Component
public class SelfupdateService {

    private static final Logger LOG = LoggerFactory.getLogger(SelfupdateService.class);

    private final BinaryChoiceService binaryChoiceService;
    private final BinaryInstallationService binaryInstallationService;

    @Autowired
    public SelfupdateService(
            final BinaryChoiceService binaryChoiceService,
            final BinaryInstallationService binaryInstallationService
    ) {
        this.binaryChoiceService = binaryChoiceService;
        this.binaryInstallationService = binaryInstallationService;
    }

    /**
     * Starts the selfupdate process to the given target version
     *
     * @param newVersion the version to which to selfupdate
     */
    public void selfupdate(final LyrebirdVersion newVersion) {
        LOG.info("Requesting selfupdate to version : {}", newVersion);

        CompletableFuture.runAsync(this::displayUpdateDownloadAlert, Platform::runLater).join();

        CompletableFuture.supplyAsync(this::getTargetPlatform)
                         .thenAcceptAsync(
                                 platform -> launchUpdate(newVersion, platform),
                                 Platform::runLater
                         );
    }

    public boolean selfupdateCompatible() {
        return binaryChoiceService.currentPlatformSupportsSelfupdate();
    }

    private void launchUpdate(LyrebirdVersion newVersion, TargetPlatform platform) {
        displayRestartAlert();
        try {
            installNewVersion(platform, newVersion);
            System.exit(0);
        } catch (IOException e) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Coult not start update, please update manually instead."
            ).showAndWait();
        }
    }

    private TargetPlatform getTargetPlatform() {
        final Option<TargetPlatform> executablePlatform = binaryChoiceService.detectRunningPlatform();
        if (executablePlatform.isEmpty()) {
            LOG.error("Lyrebird does not currently support selfupdating on this platform!");
            throw new UnsupportedOperationException("Cannot selfupdate with current binary platform!");
        }
        return executablePlatform.get();
    }

    /**
     * Launches an OS-level process to install the new version of Lyrebird
     *
     * @param platform The platform to target for the selfupdate (current one)
     * @param version  The version to target for the selfupdate
     */
    private void installNewVersion(final TargetPlatform platform, final LyrebirdVersion version)
    throws IOException {
        LOG.info("Installing new version for platform {}", platform);
        final String[] executable = binaryInstallationService.getInstallationCommandLine(platform, version);
        LOG.info("Executing : {}", (Object) executable);
        final ProcessBuilder installProcess = new ProcessBuilder(executable);
        installProcess.redirectError(ProcessBuilder.Redirect.INHERIT);
        installProcess.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        installProcess.start();
    }

    private void displayUpdateDownloadAlert() {
        final Alert selfupdateStartedAlert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Started downloading update in the background. We will tell you when it is ready !",
                ButtonType.OK
        );
        selfupdateStartedAlert.showAndWait();
    }

    /**
     * Displays an alert to the user informing them that the selfupdate is ready to start and they need to restart the
     * application after we automatically stop it.
     */
    private void displayRestartAlert() {
        LOG.debug("Displaying restart information alert!");
        final Alert restartAlert = new Alert(
                Alert.AlertType.INFORMATION,
                "Lyrebird has downloaded the update! " +
                "The application will automatically quit and start updating!",
                ButtonType.OK
        );
        restartAlert.showAndWait();
    }

}
