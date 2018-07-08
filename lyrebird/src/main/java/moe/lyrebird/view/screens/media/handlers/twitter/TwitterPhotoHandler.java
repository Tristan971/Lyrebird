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
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.view.screens.media.MediaScreenController;
import moe.lyrebird.view.screens.media.handlers.MediaHandler;
import moe.lyrebird.view.screens.media.twitter.TwitterMediaScreen;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

@Component
public class TwitterPhotoHandler implements MediaHandler<ImageView> {

    private final AsyncIO asyncIO;
    private final EasyFxml easyFxml;

    public TwitterPhotoHandler(
            final AsyncIO asyncIO,
            final EasyFxml easyFxml
    ) {
        this.asyncIO = asyncIO;
        this.easyFxml = easyFxml;
    }

    @Override
    public ImageView handleMedia(final String imageUrl) {
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
            final MediaScreenController photoController = loadResult.getController().get();
            photoController.handleMedia(imageUrl);
            Stages.stageOf("", photoViewPane)
                  .thenAcceptAsync(Stages::scheduleDisplaying);
        });
        return container;
    }

}
