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

package moe.lyrebird.view.screens.media.handlers.base;

import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.media.display.EmbeddedMediaViewHelper;
import moe.lyrebird.view.screens.media.display.MediaDisplaySceen;
import moe.lyrebird.view.screens.media.handlers.MediaHandler;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static moe.lyrebird.view.screens.media.MediaEmbeddingService.EMBEDDED_MEDIA_RECTANGLE_SIDE;

public abstract class PhotoHandler<T> implements MediaHandler<T> {

    private final AsyncIO asyncIO;
    private final EmbeddedMediaViewHelper embeddedMediaViewHelper;

    public PhotoHandler(
            final AsyncIO asyncIO,
            final EmbeddedMediaViewHelper embeddedMediaViewHelper
    ) {
        this.asyncIO = asyncIO;
        this.embeddedMediaViewHelper = embeddedMediaViewHelper;
    }

    protected Pane handleMediaSource(final String imageUrl) {
        return embeddedMediaViewHelper.makeWrapperWithIcon(
                MediaDisplaySceen.PHOTO,
                ImageResources.LOADING_REMOTE,
                imageUrl,
                imageView -> loadMiniatureAsync(imageView, imageUrl)
        );
    }

    private void loadMiniatureAsync(final ImageView imageView, final String actualImageUrl) {
        asyncIO.loadImageMiniature(actualImageUrl, EMBEDDED_MEDIA_RECTANGLE_SIDE, EMBEDDED_MEDIA_RECTANGLE_SIDE)
               .thenAcceptAsync(imageView::setImage, Platform::runLater);
    }

}
