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

package moe.lyrebird.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class LyrebirdVersion {

    private final String version;
    private final int buildVersion;
    private final String releaseUrl;
    private final List<LyrebirdPackage> packages;

    @JsonCreator
    public LyrebirdVersion(
            @JsonProperty("version") final String version,
            @JsonProperty("buildVersion") final int buildVersion,
            @JsonProperty("releaseUrl") final String releaseUrl,
            @JsonProperty("packages") final List<LyrebirdPackage> packages
    ) {
        this.version = version;
        this.buildVersion = buildVersion;
        this.releaseUrl = releaseUrl;
        this.packages = packages;
    }

    public String getVersion() {
        return version;
    }

    public int getBuildVersion() {
        return buildVersion;
    }

    public String getReleaseUrl() {
        return releaseUrl;
    }

    public List<LyrebirdPackage> getPackages() {
        return packages;
    }

    @Override
    public String toString() {
        return "LyrebirdVersion{" +
               "version='" + version + '\'' +
               ", buildVersion='" + buildVersion + '\'' +
               ", releaseUrl='" + releaseUrl + '\'' +
               ", packages=" + packages +
               '}';
    }

}
