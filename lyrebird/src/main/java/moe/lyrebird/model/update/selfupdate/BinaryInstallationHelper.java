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

import org.springframework.util.StreamUtils;
import moe.lyrebird.Lyrebird;
import moe.lyrebird.api.model.TargetPlatform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * This helper class builds the required command line setup to selfupdate on supported platforms.
 * <p>
 * Largely based off of <pre>https://github.com/bitgamma/updatefx/blob/master/src/main/java/com/briksoftware/updatefx
 * /core/InstallerService.java</pre>
 */
final class BinaryInstallationHelper {

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    private static final String LYREBIRD_DL_FOLDER = "lyrebird";

    private BinaryInstallationHelper() {
        throw new IllegalStateException("Not instianciable class.");
    }

    /**
     * Selects the appropriate command line argument generation method for the given platform.
     *
     * @param targetPlatform The platform for which to generate command line arguments
     * @param file           The file for which to generate command line arguments
     *
     * @return The appropriate selfupdate command line arguments for the given platform and package file
     */
    static String[] generateCommandLineForPlatformWithFile(
            final TargetPlatform targetPlatform,
            final File file
    ) {
        switch (targetPlatform) {
            case WINDOWS:
                return generateForWindows(file);
            case MACOS:
                return generateForMacOS(file);
            case LINUX_DEB:
            case LINUX_RPM:
            case UNIVERSAL_JAVA:
            default:
                throw new UnsupportedOperationException("Unsupported platform for self update installation execution!");
        }
    }

    /**
     * Generates the required setup for macOS selfupdate.
     *
     * @param file The DMG image file to use for selfupdate
     *
     * @return The command line arguments to selfupdate with the given file
     */
    private static String[] generateForMacOS(final File file) {
        final File tmpFolder = new File(TEMP_FOLDER, LYREBIRD_DL_FOLDER);
        final File installScriptTarget = new File(tmpFolder, "install_macos.sh");
        try {
            final String installFileData = StreamUtils.copyToString(
                    Lyrebird.class.getClassLoader().getResourceAsStream("scripts/install_macos.sh"),
                    StandardCharsets.UTF_8
            );
            Files.write(installScriptTarget.toPath(), installFileData.getBytes(), TRUNCATE_EXISTING);
            //noinspection ResultOfMethodCallIgnored
            installScriptTarget.setExecutable(true);
            //noinspection ResultOfMethodCallIgnored
            installScriptTarget.setReadable(true);
            return new String[]{
                    "/bin/sh",
                    installScriptTarget.toPath().toAbsolutePath().toString(),
                    file.toPath().toAbsolutePath().toString()
            };
        } catch (final IOException e) {
            throw new IllegalStateException("Could not copy install script to temporary folder!", e);
        }
    }

    /**
     * Generates the required command line arguments for Windows selfupdate.
     *
     * @param file The executable file to use for selfupdate (must be made via InnoSetup + JavaPackager)
     *
     * @return The required command line arguments for Windows selfupdate
     */
    private static String[] generateForWindows(final File file) {
        return new String[]{
                file.toPath().toAbsolutePath().toString(),
                "/SILENT",
                "/SP-",
                "/SUPPRESSMSGBOXES"
        };
    }

}
