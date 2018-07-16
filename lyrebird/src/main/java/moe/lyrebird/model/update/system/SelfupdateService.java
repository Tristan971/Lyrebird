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

package moe.lyrebird.model.update.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.vavr.control.Option;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import moe.lyrebird.api.server.model.objects.TargetPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

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

    public void selfupdate(final LyrebirdVersion newVersion) {
        LOG.info("Requesting selfupdate to version : {}", newVersion);

        CompletableFuture.supplyAsync(() -> {
            final Option<TargetPlatform> executablePlatform = binaryChoiceService.detectRunningPlatform();
            if (executablePlatform.isEmpty()) {
                LOG.error("Lyrebird does not currently support selfupdating on this platform!");
                throw new UnsupportedOperationException("Cannot selfupdate with current binary platform!");
            }
            return executablePlatform.get();
        }).thenAcceptAsync(platform -> {
            try {
                installNewVersion(platform, newVersion).onExit().thenAcceptAsync(installProcess -> {
                    LOG.info("Installation of new version finished!");
                    Platform.runLater(() -> {
                        displayRestartAlert();
                        LOG.info("Exiting old version of the application.");
                        Runtime.getRuntime().halt(0);
                    });
                });
            } catch (final IOException e) {
                LOG.error("Cannot install new version!", e);
                throw new IllegalStateException("Cannot install new version!", e);
            }
        });
    }

    public boolean selfupdateCompatible() {
        return binaryChoiceService.currentPlatformSupportsSelfupdate();
    }

    private Process installNewVersion(final TargetPlatform platform, final LyrebirdVersion version)
    throws IOException {
        LOG.info("Installing new version for platform {}", platform);
        final String[] executable = binaryInstallationService.getInstallationCommandLine(platform, version);
        LOG.info("Executing : {}", Arrays.toString(executable));
        return new ProcessBuilder(executable).start();
    }

    private void displayRestartAlert() {
        LOG.debug("Displaying restart information alert!");
        final Alert restartAlert = new Alert(
                Alert.AlertType.INFORMATION,
                "Lyrebird has successfully been updated! " +
                "The application will automatically quit, please reopen it afterwards :-)",
                ButtonType.OK
        );
        restartAlert.showAndWait();
    }

}
