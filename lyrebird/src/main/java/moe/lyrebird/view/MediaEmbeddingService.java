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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.util.MediaEntityType;
import twitter4j.MediaEntity;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

@Component
public class MediaEmbeddingService {

    private final AsyncIO asyncIO;

    @Autowired
    public MediaEmbeddingService(final AsyncIO asyncIO) {
        this.asyncIO = asyncIO;
    }

    public boolean isSupported(final MediaEntity entity) {
        final MediaEntityType entityType = MediaEntityType.fromTwitterType(entity.getType());
        return entityType != MediaEntityType.UNMANAGED;
    }

    public Node embed(MediaEntity entity) {
        switch (MediaEntityType.fromTwitterType(entity.getType())) {
            case PHOTO:
                return embedImage(entity.getMediaURLHttps());
            case UNMANAGED:
            default:
                throw new IllegalArgumentException("Twitter type "+entity.getType()+" is not supported!");
        }
    }

    private ImageView embedImage(final String imageUrl) {
        final ImageView container = new ImageView();
        container.setFitWidth(64);
        container.setFitHeight(64);
        asyncIO.loadImageInImageView(imageUrl, container);
        return container;
    }

}
