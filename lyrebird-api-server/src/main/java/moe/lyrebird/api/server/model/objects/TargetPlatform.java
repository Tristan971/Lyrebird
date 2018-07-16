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

package moe.lyrebird.api.server.model.objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TargetPlatform {

    WINDOWS("win", "Windows"),
    MACOS("mac", "macOS"),
    LINUX_DEB("deb", "Debian & derivatives (Ubuntu...)"),
    LINUX_RPM("rpm", "RPM-based distributions"),
    UNIVERSAL_JAVA("java", "Universal distribution (JDK required)");

    private final String codename;
    private final String readableName;

    TargetPlatform(final String codename, final String readableName) {
        this.codename = codename;
        this.readableName = readableName;
    }

    @JsonCreator
    public static TargetPlatform fromCodename(final String codename) {
        return Arrays.stream(values())
                     .filter(v -> v.codename.equals(codename))
                     .findAny()
                     .orElse(UNIVERSAL_JAVA);
    }

}
