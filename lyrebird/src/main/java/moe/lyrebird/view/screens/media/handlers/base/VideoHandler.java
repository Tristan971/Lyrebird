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

import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.media.display.MediaDisplaySceen;
import moe.lyrebird.view.screens.media.handlers.EmbeddedMediaViewHelper;
import moe.lyrebird.view.screens.media.handlers.MediaHandler;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Basic implementation for video media handlers that can extract a URL to preview.
 *
 * @param <T> The input type for the original video description type
 *
 * @see EmbeddedMediaViewHelper
 */
public abstract class VideoHandler<T> implements MediaHandler<T> {

    private final EmbeddedMediaViewHelper embeddedMediaViewHelper;

    public VideoHandler(final EmbeddedMediaViewHelper embeddedMediaViewHelper) {
        this.embeddedMediaViewHelper = embeddedMediaViewHelper;
    }

    /**
     * Implementation side of the miniature creation once the video has been mapped to an URL.
     *
     * @param mediaUrl The video described as an URL
     *
     * @return An {@link ImageView} with a static image ({@link ImageResources#VIDEO_PLAYER}). This {@link ImageView}
     * will open a {@link MediaDisplaySceen#VIDEO} screen displaying the media when it is clicked.
     */
    protected Pane handleMediaSource(final String mediaUrl) {
        return embeddedMediaViewHelper.makeWrapperWithIcon(
                MediaDisplaySceen.VIDEO,
                ImageResources.VIDEO_PLAYER,
                mediaUrl
        );
    }

}
