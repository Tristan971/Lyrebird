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

package moe.lyrebird.view;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.scene.image.Image;

@Component
public class CachedDataService {

    private static final Logger LOG = LoggerFactory.getLogger(CachedDataService.class);

    @Cacheable("userProfileImage")
    public Image userProfileImage(final User user) {
        LOG.debug("First load of user pp for user : @{}", user.getScreenName());
        return cachedImage(user.getOriginalProfileImageURLHttps());
    }

    @Cacheable("image")
    public Image cachedImage(final String imageUrl) {
        LOG.debug("Loading and caching image at URL : {}", imageUrl);
        return new Image(imageUrl);
    }

}
