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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import moe.lyrebird.view.screens.media.handlers.direct.DirectImageHandler;
import moe.lyrebird.view.screens.media.handlers.twitter.TwitterMediaEntity;
import moe.lyrebird.view.screens.media.handlers.twitter.TwitterVideoHandler;
import twitter4a.MediaEntity;
import twitter4a.Status;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service is in charge of creating preview {@link ImageView} for embedded images (and media in general) that a
 * tweet can contain.
 *
 * @see moe.lyrebird.view.screens.media.handlers
 * @see TwitterMediaEntity
 */
@Component
public class MediaEmbeddingService {

    public static final double EMBEDDED_MEDIA_RECTANGLE_SIDE = 64.0;
    public static final double EMBEDDED_MEDIA_RECTANGLE_CORNER_RADIUS = 10.0;

    private final DirectImageHandler directPhotoHandler;
    private final TwitterVideoHandler twitterVideoHandler;

    public MediaEmbeddingService(
            final DirectImageHandler directPhotoHandler,
            final TwitterVideoHandler twitterVideoHandler
    ) {
        this.directPhotoHandler = directPhotoHandler;
        this.twitterVideoHandler = twitterVideoHandler;
    }

    /**
     * Generates a cached list of appropriate nodes (usually {@link ImageView}) to preview the embedded media in the
     * given status.
     *
     * @param status The status to generate previews for.
     *
     * @return The list of previews generated. Called only once for every tweet since it is cached via {@link
     * Cacheable}.
     */
    public List<Node> embed(final Status status) {
        return Arrays.stream(status.getMediaEntities())
                     .filter(TwitterMediaEntity::isSupported)
                     .map(this::embedOne)
                     .collect(Collectors.toList());
    }

    /**
     * Generates preview for a single Twitter-side {@link MediaEntity} matching it agains the {@link TwitterMediaEntity}
     * enumeration.
     *
     * @param entity The entity whose preview will be generated.
     *
     * @return The preview for the given entity.
     * @throws IllegalArgumentException when an unsupported entity is given since it should only be called on valid
     *                                  entities.
     */
    private Node embedOne(final MediaEntity entity) {
        switch (TwitterMediaEntity.fromTwitterType(entity.getType())) {
            case PHOTO:
                return directPhotoHandler.handleMedia(entity.getMediaURLHttps());
            case VIDEO:
            case ANIMATED_GIF:
                return twitterVideoHandler.handleMedia(entity.getVideoVariants());
            case UNMANAGED:
            default:
                throw new IllegalArgumentException("Twitter type " + entity.getType() + " is not supported!");
        }
    }
}
