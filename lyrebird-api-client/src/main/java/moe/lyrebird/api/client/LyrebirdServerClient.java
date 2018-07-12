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

package moe.lyrebird.api.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static moe.lyrebird.api.server.controllers.Endpoints.VERSIONS_CHANGENOTES;
import static moe.lyrebird.api.server.controllers.Endpoints.VERSIONS_CONTROLLER;
import static moe.lyrebird.api.server.controllers.Endpoints.VERSIONS_LATEST;

@Component
public class LyrebirdServerClient {

    private static final Logger LOG = LoggerFactory.getLogger(LyrebirdServerClient.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;

    @Autowired
    public LyrebirdServerClient(final RestTemplate restTemplate, final Environment environment) {
        this.restTemplate = restTemplate;
        this.apiUrl = environment.getRequiredProperty("api.url");
    }

    public LyrebirdVersion getLatestVersion() {
        LOG.info("Loading latest version from server");
        return restTemplate.getForEntity(
                buildUrl(VERSIONS_CONTROLLER, VERSIONS_LATEST),
                LyrebirdVersion.class
        ).getBody();
    }

    public String getChangeNotes(final String buildVersion) {
        LOG.info("Loading changenotes for buildVersion {}", buildVersion);
        return restTemplate.getForObject(
                buildUrl(VERSIONS_CONTROLLER, VERSIONS_CHANGENOTES),
                String.class,
                buildVersion
        );
    }

    private String buildUrl(final String controller, final String method) {
        return apiUrl + "/" + controller + "/" + method;
    }

}
