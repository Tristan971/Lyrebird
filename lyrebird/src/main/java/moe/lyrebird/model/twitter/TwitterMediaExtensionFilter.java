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

package moe.lyrebird.model.twitter;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.FileChooser.ExtensionFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TwitterMediaExtensionFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterMediaExtensionFilter.class);

    public final ExtensionFilter extensionFilter;

    public TwitterMediaExtensionFilter(Environment environment) {
        this.extensionFilter = buildExtensionFilter(environment);
    }

    private static ExtensionFilter buildExtensionFilter(final Environment environment) {
        final String allowedExtensionsStr = environment.getRequiredProperty("twitter.media.allowedExtensions");
        final List<String> allowedExtensions = Arrays.stream(allowedExtensionsStr.split(","))
                                     .map(ext -> "*." + ext)
                                     .collect(Collectors.toList());

        LOG.debug("Allowed media formats for tweet attachments are : {}", allowedExtensions);
        return new ExtensionFilter("Supported media types", allowedExtensions);
    }

}
