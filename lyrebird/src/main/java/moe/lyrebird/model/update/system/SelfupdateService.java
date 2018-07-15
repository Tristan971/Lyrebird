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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Component
public class SelfupdateService {

    private static final Logger LOG = LoggerFactory.getLogger(SelfupdateService.class);

    private final DistribuableBinaryPlatformService distribuableBinaryPlatformService;
    private final DistribuableExecutionService distribuableExecutionService;
    private final DistribuableInstallationService distribuableInstallationService;

    @Autowired
    public SelfupdateService(
            final DistribuableBinaryPlatformService distribuableBinaryPlatformService,
            final DistribuableExecutionService distribuableExecutionService,
            final DistribuableInstallationService distribuableInstallationService
    ) {
        this.distribuableBinaryPlatformService = distribuableBinaryPlatformService;
        this.distribuableExecutionService = distribuableExecutionService;
        this.distribuableInstallationService = distribuableInstallationService;
    }

    public void selfupdate(final LyrebirdVersion newVersion) {
        LOG.info("Requesting selfupdate to version : {}", newVersion);

        CompletableFuture.supplyAsync(() -> {
            final Option<TargetPlatform> executablePlatform = distribuableBinaryPlatformService.detectRunningPlatform();
            if (executablePlatform.isEmpty()) {
                LOG.error("Lyrebird does not currently support selfupdating on this platform!");
                throw new UnsupportedOperationException("Cannot selfupdate with current binary platform!");
            }
            return executablePlatform.get();
        }).thenComposeAsync(platform -> {
            try {
                return installNewVersion(platform, newVersion);
            } catch (IOException e) {
                LOG.error("Cannot install new version!", e);
                throw new IllegalStateException("Cannot install new version!", e);
            }
        }).thenComposeAsync(installProcess -> {
            LOG.info("Installed new version!");
            try {
                return restartApplication();
            } catch (IOException e) {
                LOG.error("Cannot restart application!");
                throw new IllegalStateException("Cannot restart application!", e);
            }
        }).thenAcceptAsync(restartProcess -> {
            LOG.info("Exiting old version of the application.");
            Runtime.getRuntime().halt(0);
        });
    }

    private CompletableFuture<Process> installNewVersion(final TargetPlatform platform, final LyrebirdVersion version)
    throws IOException {
        LOG.info("Installing new version for platform {}", platform);
        final String[] executable = distribuableInstallationService.getInstallationCommandLine(platform, version);
        LOG.info("Executing : {}", Arrays.toString(executable));
        return new ProcessBuilder(executable).start().onExit();
    }

    private CompletableFuture<Process> restartApplication() throws IOException {
        LOG.info("Restarting Lyrebird!");
        final String[] executable = distribuableExecutionService.getExecutionCommandLine();
        LOG.info("Executing : {}", Arrays.toString(executable));
        return new ProcessBuilder(executable).start().onExit();
    }

}
