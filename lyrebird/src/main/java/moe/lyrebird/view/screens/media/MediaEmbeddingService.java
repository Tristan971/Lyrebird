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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.beanmanagement.Selector;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.media.MediaEntityType;
import moe.lyrebird.view.screens.media.twitter.TwitterMediaScreen;
import twitter4j.MediaEntity;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.function.Function;

@Component
public class MediaEmbeddingService {

    private final AsyncIO asyncIO;
    private final EasyFxml easyFxml;
    private final StageManager stageManager;

    @Autowired
    public MediaEmbeddingService(
            final AsyncIO asyncIO,
            final EasyFxml easyFxml,
            final StageManager stageManager
    ) {
        this.asyncIO = asyncIO;
        this.easyFxml = easyFxml;
        this.stageManager = stageManager;
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

        container.setOnMouseClicked(e -> {
            final FxmlLoadResult<Pane, MediaScreenController> loadResult = easyFxml.loadNode(
                    TwitterMediaScreen.PHOTO,
                    Pane.class,
                    MediaScreenController.class
            );
            final Pane photoViewPane = loadResult.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
            final MediaScreenController photoController =
                    loadResult.getController()
                              .getOrElseThrow((Function<? super Throwable, RuntimeException>) RuntimeException::new);
            photoController.setMediaUrl(imageUrl);
            Stages.stageOf("", photoViewPane)
                  .thenAcceptAsync(stage -> {
                      stageManager.registerMultiple(
                              TwitterMediaScreen.PHOTO,
                              new Selector(photoController.hashCode()),
                              stage
                      );
                      Stages.scheduleDisplaying(stage);
                  });
        });
        return container;
    }

}
