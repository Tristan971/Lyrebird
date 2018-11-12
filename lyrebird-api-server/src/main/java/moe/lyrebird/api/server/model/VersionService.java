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

package moe.lyrebird.api.server.model;

import static io.vavr.API.unchecked;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import moe.lyrebird.api.model.LyrebirdVersion;

@Component
public class VersionService {

    private static final String VERSIONS_PATTERN = "classpath:versions/*.json";

    private final ObjectMapper objectMapper;

    @Autowired
    public VersionService(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Cacheable("latestVersion")
    public LyrebirdVersion getLatestVersion() {
        return getAllVersions().stream()
                               .max(Comparator.comparingInt(LyrebirdVersion::getBuildVersion))
                               .orElse(null);
    }

    @Cacheable("availableVersions")
    public List<LyrebirdVersion> getAllVersions() {
        final PathMatchingResourcePatternResolver versionsResourcesResolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] versionResources = versionsResourcesResolver.getResources(VERSIONS_PATTERN);
            return Arrays.stream(versionResources)
                         .map(unchecked(Resource::getInputStream))
                         .map(unchecked(is -> objectMapper.readValue(is, LyrebirdVersion.class)))
                         .sorted(Comparator.comparing(LyrebirdVersion::getBuildVersion).reversed())
                         .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new IllegalStateException("Can not load releases!", e);
        }
    }

}
