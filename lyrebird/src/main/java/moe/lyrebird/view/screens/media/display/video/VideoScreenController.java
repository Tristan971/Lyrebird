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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VideoScreenController extends MediaScreenController {

    private static final Logger LOG = LoggerFactory.getLogger(VideoScreenController.class);

    private static final Media TEMPORARY_MEDIA_LOAD = MediaResources.LOADING_REMOTE_GTARD.getMedia();

    @FXML
    private AnchorPane container;

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
        bindViewSizeToParent();
        mediaView.setSmooth(true);
        mediaProperty.addListener((o, temp, actual) -> openMediaPlayer());
    }

    @Override
    public void handleMedia(String mediaUrl) {
        asyncIO.loadMedia(mediaUrl).thenAcceptAsync(mediaProperty::setValue, Platform::runLater);
    }

    @Override
    protected void bindViewSizeToParent() {
        mediaView.fitHeightProperty().bind(container.heightProperty());
        mediaView.fitWidthProperty().bind(container.widthProperty());
    }

    private void openMediaPlayer() {
        CompletableFuture.supplyAsync(() -> new MediaPlayer(mediaProperty.getValue()))
                         .thenAcceptAsync(mediaPlayer -> {
                             mediaPlayer.setAutoPlay(true);
                             mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
                             mediaView.setOnMouseClicked(e -> onClickHandler(mediaPlayer));
                             mediaView.setMediaPlayer(mediaPlayer);
                         }, Platform::runLater);
    }

    private void onClickHandler(final MediaPlayer mediaPlayer) {
        final MediaPlayer.Status playerStatus = mediaPlayer.statusProperty().get();
        switch (playerStatus) {
            case READY:
                mediaPlayer.play();
                break;
            case PAUSED:
                mediaPlayer.play();
                break;
            case PLAYING:
                mediaPlayer.pause();
                break;
            case STOPPED:
                mediaPlayer.play();
                break;
            case HALTED:
            case UNKNOWN:
            case STALLED:
            case DISPOSED:
                LOG.warn("Media player issue ! [status = {}]", playerStatus);
            default:
                LOG.error("UNKNOWN MEDIA PLAYER STATUS ! [status = {}]", playerStatus);
        }
    }

    @Override
    public void setStage(Stage embeddingStage) {
        embeddingStage.setOnCloseRequest(e -> {
            final MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
        });
    }
}
