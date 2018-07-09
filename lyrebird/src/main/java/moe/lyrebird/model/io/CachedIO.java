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

@Component
public class CachedIO {

    private static final Logger LOG = LoggerFactory.getLogger(CachedIO.class);

    @Cacheable("image")
    public Image loadImage(final String imageUrl) {
        LOG.trace("First load of image {}", imageUrl);
        return new Image(imageUrl);
    }

    @Cacheable("imageMiniature")
    public Image loadImageMiniature(final String imageUrl, final double width, final double heigth) {
        LOG.trace("First load of miniature image {} [width = {}, heigth = {}]", imageUrl, width, heigth);
        return new Image(imageUrl, width, heigth, false, true);
    }

}
