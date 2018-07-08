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

package moe.lyrebird.view.screens.media;

import org.springframework.stereotype.Component;
import moe.lyrebird.view.screens.media.handlers.twitter.TwitterPhotoHandler;
import twitter4j.MediaEntity;

import javafx.scene.Node;

@Component
public class MediaEmbeddingService {

    private final TwitterPhotoHandler twitterPhotoHandler;

    public MediaEmbeddingService(TwitterPhotoHandler twitterPhotoHandler) {
        this.twitterPhotoHandler = twitterPhotoHandler;
    }

    public boolean isSupported(final MediaEntity entity) {
        final MediaEntityType entityType = MediaEntityType.fromTwitterType(entity.getType());
        return entityType != MediaEntityType.UNMANAGED;
    }

    public Node embed(MediaEntity entity) {
        switch (MediaEntityType.fromTwitterType(entity.getType())) {
            case PHOTO:
                return twitterPhotoHandler.handleMedia(entity.getMediaURLHttps());
            case UNMANAGED:
            default:
                throw new IllegalArgumentException("Twitter type "+entity.getType()+" is not supported!");
        }
    }

}
