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

package moe.lyrebird.view.screens.media.handlers.direct;

import org.springframework.stereotype.Component;

import javafx.scene.layout.Pane;

import moe.lyrebird.view.screens.media.display.video.VideoScreenComponent;
import moe.lyrebird.view.screens.media.handlers.EmbeddedMediaViewHelper;
import moe.lyrebird.view.screens.media.handlers.base.VideoHandler;

/**
 * Implementation of {@link VideoHandler} for videos already in URL form.
 * <p>
 * Simple pass-through.
 */
@Component
public class DirectVideoHandler extends VideoHandler<String> {

    public DirectVideoHandler(EmbeddedMediaViewHelper embeddedMediaViewHelper, VideoScreenComponent videoScreenComponent) {
        super(embeddedMediaViewHelper, videoScreenComponent);
    }

    @Override
    public Pane handleMedia(final String mediaSource) {
        return handleMediaSource(mediaSource);
    }

}
