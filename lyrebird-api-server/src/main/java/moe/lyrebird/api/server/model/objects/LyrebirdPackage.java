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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public final class LyrebirdPackage {

    private final TargetPlatform targetPlatform;
    private final URL packageUrl;

    @JsonCreator
    public LyrebirdPackage(
            @JsonProperty("targetPlatform") final TargetPlatform targetPlatform,
            @JsonProperty("packageUrl") final URL packageUrl
    ) {
        this.targetPlatform = targetPlatform;
        this.packageUrl = packageUrl;
    }

    public TargetPlatform getTargetPlatform() {
        return targetPlatform;
    }

    public URL getPackageUrl() {
        return packageUrl;
    }

}
