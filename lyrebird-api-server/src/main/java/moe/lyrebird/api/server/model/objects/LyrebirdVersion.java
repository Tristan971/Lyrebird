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

import org.springframework.util.StreamUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LyrebirdVersion {

    private final String version;
    private final String buildVersion;
    private final String changenotesFile;
    private final List<LyrebirdPackage> packages;

    @JsonCreator
    public LyrebirdVersion(
            @JsonProperty("version") final String version,
            @JsonProperty("buildVersion") final String buildVersion,
            @JsonProperty("changenotesFile") final String changenotesFile,
            @JsonProperty("packages") final List<LyrebirdPackage> packages
    ) {
        this.version = version;
        this.buildVersion = buildVersion;
        this.packages = packages;
        this.changenotesFile = changenotesFile;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getChangenotes() {
        try (InputStream fis = getClass().getClassLoader().getResourceAsStream("versions/" + changenotesFile)) {
            return StreamUtils.copyToString(fis, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "An error happened while loading changenotes.";
        }
    }

    public List<LyrebirdPackage> getPackages() {
        return packages;
    }

    @Override
    public String toString() {
        return "LyrebirdVersion{" +
               "version='" + version + '\'' +
               ", buildVersion='" + buildVersion + '\'' +
               ", changenotesFile='" + changenotesFile + '\'' +
               ", packages=" + packages +
               '}';
    }
}
