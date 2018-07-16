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

package moe.lyrebird.model.update.system.installation;

import moe.lyrebird.api.server.model.objects.TargetPlatform;
import oshi.SystemInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Largely based off of <pre>https://github.com/bitgamma/updatefx/blob/master/src/main/java/com/briksoftware/updatefx/core/InstallerService.java</pre>
 */
public final class InstallationExecutableHelper {

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    private static final String LYREBIRD_DL_FOLDER = "lyrebird";
    private static final int LYREBIRD_PROCESS_ID = new SystemInfo().getOperatingSystem().getProcessId();

    private InstallationExecutableHelper() {
        throw new IllegalStateException("Not instianciable class.");
    }

    public static String[] generateCommandLineForPlatformWithFile(
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String[] generateForMacOS(final File file) {
        final File tmpFolder = new File(TEMP_FOLDER, LYREBIRD_DL_FOLDER);
        final File installFile = new File(tmpFolder, "install.sh");
        try {
            Files.copy(file.toPath(), installFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            installFile.setExecutable(true);
            installFile.setReadable(true);
            return new String[]{
                    "/bin/sh",
                    installFile.toPath().toAbsolutePath().toString(),
                    file.toPath().toAbsolutePath().toString(),
                    String.format("%d", LYREBIRD_PROCESS_ID)
            };
        } catch (final IOException e) {
            throw new IllegalStateException("Could not copy install script to temporary folder!", e);
        }
    }

    private static String[] generateForWindows(final File file) {
        return new String[]{
                file.toPath().toAbsolutePath().toString(),
                "/SILENT",
                "/SP-",
                "/SUPPRESSMSGBOXES"
        };
    }

}
