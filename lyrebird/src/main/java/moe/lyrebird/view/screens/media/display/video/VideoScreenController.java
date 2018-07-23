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
import moe.lyrebird.view.screens.media.display.MediaScreenController;
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

/**
 * This class manages a video's detailed view
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VideoScreenController implements MediaScreenController {

    private static final Logger LOG = LoggerFactory.getLogger(VideoScreenController.class);

    private static final Media TEMPORARY_MEDIA_LOAD = MediaResources.LOADING_REMOTE_GTARD.getMedia();

    @FXML
    private AnchorPane container;

    @FXML
    private MediaView mediaView;

    private final AsyncIO asyncIO;

    private final ObjectProperty<Media> mediaProperty;

    @Autowired
    public VideoScreenController(final AsyncIO asyncIO) {
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
    public void handleMedia(final String mediaUrl) {
        asyncIO.loadMedia(mediaUrl).thenAcceptAsync(mediaProperty::setValue, Platform::runLater);
    }

    @Override
    public void bindViewSizeToParent() {
        mediaView.fitHeightProperty().bind(container.heightProperty());
        mediaView.fitWidthProperty().bind(container.widthProperty());
    }

    /**
     * Opens a backing {@link MediaPlayer} for the embedded video.
     */
    private void openMediaPlayer() {
        CompletableFuture.supplyAsync(() -> new MediaPlayer(mediaProperty.getValue()))
                         .thenAcceptAsync(mediaPlayer -> {
                             mediaPlayer.setAutoPlay(true);
                             mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
                             mediaView.setOnMouseClicked(e -> onClickHandler(mediaPlayer));
                             mediaView.setMediaPlayer(mediaPlayer);
                         }, Platform::runLater);
    }

    /**
     * Sets up reasonable settings for {@link MediaPlayer} behavior when it comes to user interactions.
     * <p>
     * Basically the same as any wide-usage video player with replaying when unpausing after a video ended.
     *
     * @param mediaPlayer The media player to setup.
     */
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
                LOG.warn("Media player was already disposed! [status = {}]", playerStatus);
                break;
            default:
                LOG.error("UNKNOWN MEDIA PLAYER STATUS ! [status = {}]", playerStatus);
        }
    }

    /**
     * Makes sure that this media player is stopped and disposed when the user closes the stage for this screen.
     *
     * @param embeddingStage The stage for which to listen to close requests
     */
    @Override
    public void setStage(final Stage embeddingStage) {
        embeddingStage.setOnCloseRequest(e -> {
            final MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
        });
    }
}
