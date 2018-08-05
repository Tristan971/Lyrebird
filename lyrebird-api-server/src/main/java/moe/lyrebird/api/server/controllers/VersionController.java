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

package moe.lyrebird.api.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import moe.lyrebird.api.model.LyrebirdVersion;
import moe.lyrebird.api.server.model.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static moe.lyrebird.api.conf.Endpoints.VERSIONS_CHANGENOTES;
import static moe.lyrebird.api.conf.Endpoints.VERSIONS_CONTROLLER;
import static moe.lyrebird.api.conf.Endpoints.VERSIONS_LATEST;

@RestController
@RequestMapping(VERSIONS_CONTROLLER)
public class VersionController {

    private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

    private final VersionService versionService;

    @Autowired
    public VersionController(final VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping(value = VERSIONS_LATEST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LyrebirdVersion getLatestVersion() {
        return versionService.getLatestVersion();
    }

    @GetMapping(value = VERSIONS_CHANGENOTES, produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String getChangeNotes(@PathVariable final int buildVersion) {
        try (final InputStream fis = getClass().getClassLoader().getResourceAsStream("versions/" + buildVersion + ".md")) {
            return StreamUtils.copyToString(fis, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            LOG.error("Could not load changenotes for build version "+buildVersion, e);
            return "Could not load changenotes.";
        }
    }

}
