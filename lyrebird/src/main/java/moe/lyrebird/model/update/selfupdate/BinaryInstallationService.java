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

import org.springframework.stereotype.Component;
import moe.lyrebird.api.model.LyrebirdPackage;
import moe.lyrebird.api.model.LyrebirdVersion;
import moe.lyrebird.api.model.TargetPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * This service, with the help of the {@link BinaryInstallationHelper}, generates the command line arguments for the
 * current platform to execute a selfupdate.
 *
 * @see BinaryInstallationHelper
 */
@Component
public class BinaryInstallationService {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryInstallationService.class);

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    private static final String LYREBIRD_DL_FOLDER = "lyrebird";

    /**
     * Determines the correct command line arguments for selfupdating a given platform's executable to a given version.
     *
     * @param targetPlatform  The platform on which the selfupdate shall be performed
     * @param lyrebirdVersion The version to which the selfupdate shall be performed
     *
     * @return The command line arguments for the selfupdate execution
     */
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

        return BinaryInstallationHelper.generateCommandLineForPlatformWithFile(targetPlatform, downloadedBinary);
    }

    /**
     * Finds the correct {@link LyrebirdPackage} for the given target platform.
     *
     * @param lyrebirdVersion The version to search a package from
     * @param targetPlatform  The platform to search a package for
     *
     * @return The package if it was found, else {@link Optional#empty()}.
     */
    private Optional<LyrebirdPackage> findPackageForPlatform(
            final LyrebirdVersion lyrebirdVersion,
            final TargetPlatform targetPlatform
    ) {
        return lyrebirdVersion.getPackages()
                              .stream()
                              .filter(distribuable -> distribuable.getTargetPlatform().equals(targetPlatform))
                              .findAny();
    }

    /**
     * Downloads a binary file locally to a temporary location that is platform dependent.
     *
     * @param binaryUrl The file to download's URL
     *
     * @return The downloaded file
     */
    private File downloadBinary(final URL binaryUrl) {
        final File targetFile = prepareTargetFile(binaryUrl);
        LOG.debug("Downloading {} to file {}", binaryUrl, targetFile);

        final long downloadedFileSize = downloadFileImpl(binaryUrl, targetFile);
        LOG.debug("Saved file {} to {} with size of {} bytes", binaryUrl, targetFile, downloadedFileSize);

        return targetFile;
    }

    /**
     * Prepares the location of the downloaded installation binary in the system's temporary folder.
     *
     * @param binaryUrl The URL of the binary (to match name mostly)
     *
     * @return The premade File reference to the location to which to download
     */
    private File prepareTargetFile(final URL binaryUrl) {
        final String[] binaryUrlSplit = binaryUrl.toExternalForm().split("/");
        final String binaryFilename = binaryUrlSplit[binaryUrlSplit.length - 1];

        final File tmpFolder = new File(TEMP_FOLDER, LYREBIRD_DL_FOLDER);
        if (tmpFolder.mkdirs()) {
            LOG.debug("Created temporary file download folder at {}", tmpFolder);
        }

        return new File(tmpFolder, binaryFilename);
    }

    /**
     * Downloads a file from a URL to a local location.
     *
     * @param binaryUrl  The URL of the file to download
     * @param targetFile The location where to save the downloaded file
     *
     * @return The size of the downloaded file in bytes
     */
    private long downloadFileImpl(final URL binaryUrl, final File targetFile) {
        try (final InputStream binaryInputStream = binaryUrl.openStream()) {
            return Files.copy(binaryInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            LOG.error("Could not download binary!", e);
            throw new IllegalStateException("Could not download binary! [" + binaryUrl.toExternalForm() + "]", e);
        }
    }

}
