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

package moe.lyrebird.view.screens.media.handlers.twitter;

import org.springframework.stereotype.Component;
import moe.lyrebird.view.screens.media.display.EmbeddedMediaViewHelper;
import moe.lyrebird.view.screens.media.handlers.base.VideoHandler;
import twitter4j.MediaEntity;

import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.Comparator;

@Component
public class TwitterVideoHandler extends VideoHandler<MediaEntity.Variant[]> {

    public TwitterVideoHandler(EmbeddedMediaViewHelper embeddedMediaViewHelper) {
        super(embeddedMediaViewHelper);
    }

    @Override
    public Pane handleMedia(MediaEntity.Variant[] mediaSource) {
        final var best = Arrays.stream(mediaSource).max(Comparator.comparingInt(MediaEntity.Variant::getBitrate));
        final MediaEntity.Variant bestVersion = best.orElseThrow();
        return handleMediaSource(bestVersion.getUrl());
    }

}
