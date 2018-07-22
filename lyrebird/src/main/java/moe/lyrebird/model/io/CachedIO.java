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

package moe.lyrebird.model.io;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * This class exposes convenience methods for doing cached IO operations.
 */
@Component
public class CachedIO {

    private static final Logger LOG = LoggerFactory.getLogger(CachedIO.class);

    /**
     * Loads and caches an image.
     *
     * @param imageUrl The url of this image
     *
     * @return This image loaded in an {@link Image} instance.
     */
    @Cacheable(value = "image", sync = true)
    public Image loadImage(final String imageUrl) {
        LOG.trace("First load of image {}", imageUrl);
        return new Image(imageUrl);
    }

    /**
     * Loads and caches an image as a miniature.
     *
     * @param imageUrl The url of this image
     * @param width    The width to miniaturize this image to
     * @param heigth   The heigth to miniaturize this image to
     *
     * @return This image's miniature loaded in an {@link Image} instance.
     */
    @Cacheable(value = "imageMiniature", sync = true)
    public Image loadImageMiniature(final String imageUrl, final double width, final double heigth) {
        LOG.trace("First load of miniature image {} [width = {}, heigth = {}]", imageUrl, width, heigth);
        return new Image(imageUrl, width, heigth, false, true);
    }

    /**
     * Loads and caches a media file.
     *
     * @param mediaUrl The url of this media file
     *
     * @return This media loaded in a {@link Media} instance.
     */
    @Cacheable(value = "mediaFile", sync = true)
    public Media loadMediaFile(final String mediaUrl) {
        LOG.trace("First load of media {}", mediaUrl);
        return new Media(mediaUrl);
    }

}
