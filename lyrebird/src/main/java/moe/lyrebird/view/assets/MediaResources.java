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

package moe.lyrebird.view.assets;

import javafx.scene.media.Media;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * This enum contains media resources used either as placeholder or fallback.
 */
public enum MediaResources {
    LOADING_REMOTE_GTARD("loading-gtard.mp4");

    private final Media media;

    /**
     * @param path The path of the resource relative to src/main/resources/assets/video
     */
    MediaResources(final String path) {
        try {
            this.media = loadMediaFile(path);
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Could not load required temporary media resource!", e);
        }
    }

    /**
     * This method is the load call executed on constructor call to preload the media on startup.
     *
     * @param path The path from the enum member declaration.
     *
     * @return The underlying media that will actually get used.
     */
    private static Media loadMediaFile(final String path) throws URISyntaxException {
        final String root = "assets/video/";
        final ClassLoader classLoader = MediaResources.class.getClassLoader();
        final URI mediaUri = Objects.requireNonNull(classLoader.getResource(root + path)).toURI();
        return new Media(mediaUri.toString());
    }

    public Media getMedia() {
        return media;
    }

}
