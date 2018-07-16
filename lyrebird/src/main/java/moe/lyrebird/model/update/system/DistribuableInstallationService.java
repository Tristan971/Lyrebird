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

import org.springframework.stereotype.Component;
import moe.lyrebird.api.server.model.objects.LyrebirdPackage;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import moe.lyrebird.api.server.model.objects.TargetPlatform;
import moe.lyrebird.model.update.system.installation.InstallationExecutableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Component
public class DistribuableInstallationService {

    private static final Logger LOG = LoggerFactory.getLogger(DistribuableInstallationService.class);

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    private static final String LYREBIRD_DL_FOLDER = "lyrebird";

    String[] getInstallationCommandLine(
            final TargetPlatform targetPlatform,
            final LyrebirdVersion lyrebirdVersion
    ) {
        final Optional<LyrebirdPackage> acceptablePackage = findPackageForPlatform(lyrebirdVersion, targetPlatform);
        if (!acceptablePackage.isPresent()) {
            throw new IllegalStateException(
                    "Could not find an acceptable package for platform " + targetPlatform +
                    " in version : " + lyrebirdVersion
            );
        }

        final LyrebirdPackage lyrebirdPackage = acceptablePackage.get();
        final File downloadedBinary = downloadBinary(lyrebirdPackage.getPackageUrl());

        return InstallationExecutableHelper.generateCommandLineForPlatformWithFile(targetPlatform, downloadedBinary);
    }

    private Optional<LyrebirdPackage> findPackageForPlatform(
            final LyrebirdVersion lyrebirdVersion,
            final TargetPlatform targetPlatform
    ) {
        return lyrebirdVersion.getPackages()
                              .stream()
                              .filter(distribuable -> distribuable.getTargetPlatform().equals(targetPlatform))
                              .findAny();
    }

    private File downloadBinary(final URL binaryUrl) {
        final File targetFile = prepareTargetFile(binaryUrl);
        LOG.debug("Downloading {} to file {}", binaryUrl, targetFile);

        final long downloadedFileSize = downloadFileImpl(binaryUrl, targetFile);
        LOG.debug("Saved file {} to {} with size of {} bytes", binaryUrl, targetFile, downloadedFileSize);

        return targetFile;
    }

    private File prepareTargetFile(final URL binaryUrl) {
        final String[] binaryUrlSplit = binaryUrl.toExternalForm().split("/");
        final String binaryFilename = binaryUrlSplit[binaryUrlSplit.length - 1];

        final File tmpFolder = new File(TEMP_FOLDER, LYREBIRD_DL_FOLDER);
        if (tmpFolder.mkdirs()) {
            LOG.debug("Created temporary file download folder at {}", tmpFolder);
        }

        return new File(tmpFolder, binaryFilename);
    }

    private long downloadFileImpl(final URL binaryUrl, final File targetFile) {
        try (final InputStream binaryInputStream = binaryUrl.openStream()) {
            return Files.copy(binaryInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            LOG.error("Could not download binary!", e);
            throw new IllegalStateException("Could not download binary! [" + binaryUrl.toExternalForm() + "]", e);
        }
    }

}
