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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * This class exposes convenience method to execute asynchronous network operations on the asyncIo thread.
 * <p>
 * Every call here must be explicitly made with the {@link CachedMedia} service until proven that the cache really grows
 * too big with that rule.
 */
@Component
public class AsyncIO {

    private static final Executor ASYNC_IO_EXECUTOR = Executors.newFixedThreadPool(4);

    private final CachedMedia cachedMedia;

    @Autowired
    public AsyncIO(final CachedMedia cachedMedia) {
        this.cachedMedia = cachedMedia;
    }

    /**
     * Asynchronously loads an image.
     *
     * @param imageUrl the image to load's url as a string
     *
     * @return A {@link CompletableFuture} which can be asynchronously consumed if needed upon termination of this load
     * operation.
     */
    public CompletableFuture<Image> loadImage(final String imageUrl) {
        return CompletableFuture.supplyAsync(() -> cachedMedia.loadImage(imageUrl), ASYNC_IO_EXECUTOR);
    }

    /**
     * Asynchronously loads an image in miniature version.
     *
     * @param imageUrl the image to load's url as a string
     * @param width    the width of the miniature
     * @param height   the height of the miniature
     *
     * @return A {@link CompletableFuture} which can be asynchronously consumed if needed upon termination of this load
     * operation.
     */
    public CompletableFuture<Image> loadImageMiniature(final String imageUrl, final double width, final double height) {
        return CompletableFuture.supplyAsync(
                () -> cachedMedia.loadImageMiniature(imageUrl, width, height),
                ASYNC_IO_EXECUTOR
        );
    }

    /**
     * Asynchronously loads a given media.
     *
     * @param mediaUrl The loaded media's string-represented url
     *
     * @return A {@link CompletableFuture} which can be asynchronously consumed if needed upon termination of this load
     * operation.
     */
    public CompletableFuture<Media> loadMedia(final String mediaUrl) {
        return CompletableFuture.supplyAsync(
                () -> cachedMedia.loadMediaFile(mediaUrl),
                ASYNC_IO_EXECUTOR
        );
    }

}
