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

package moe.lyrebird.view.screens.media.display.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.view.assets.MediaResources;
import moe.lyrebird.view.screens.media.MediaScreenController;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VideoScreenController extends MediaScreenController {

    private static final Media TEMPORARY_MEDIA_LOAD = MediaResources.LOADING_REMOTE_GTARD.getMedia();

    @FXML
    private MediaView mediaView;

    private final AsyncIO asyncIO;

    private final ObjectProperty<Media> mediaProperty;

    @Autowired
    public VideoScreenController(AsyncIO asyncIO) {
        this.asyncIO = asyncIO;
        mediaProperty = new SimpleObjectProperty<>(TEMPORARY_MEDIA_LOAD);
    }

    @Override
    public void initialize() {
        mediaView.setSmooth(true);
        mediaView.setMediaPlayer(openMediaPlayer());
        mediaProperty.addListener((o, temp, actual) -> mediaView.setMediaPlayer(openMediaPlayer()));
    }

    @Override
    public void handleMedia(String mediaUrl) {
        asyncIO.loadMedia(mediaUrl).thenAcceptAsync(mediaProperty::setValue, Platform::runLater);
    }

    private MediaPlayer openMediaPlayer() {
        return new MediaPlayer(mediaProperty.getValue());
    }

}
