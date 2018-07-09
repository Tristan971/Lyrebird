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

package moe.lyrebird.view.screens.media.display;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.media.MediaScreenController;
import moe.lyrebird.view.util.Clipping;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.function.Consumer;

import static moe.lyrebird.view.screens.media.MediaEmbeddingService.EMBEDDED_MEDIA_RECTANGLE_CORNER_RADIUS;
import static moe.lyrebird.view.screens.media.MediaEmbeddingService.EMBEDDED_MEDIA_RECTANGLE_SIDE;

@Component
public class EmbeddedMediaViewHelper {

    private final EasyFxml easyFxml;

    public EmbeddedMediaViewHelper(EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    @SafeVarargs
    public final Pane makeWrapperWithIcon(
            final MediaDisplaySceen displayScreen,
            final ImageResources imageResource,
            final String mediaUrl,
            final Consumer<ImageView>... andThen
    ) {
        final Pane containerPane = new Pane();
        containerPane.setPrefWidth(EMBEDDED_MEDIA_RECTANGLE_SIDE);
        containerPane.setPrefHeight(EMBEDDED_MEDIA_RECTANGLE_SIDE);

        final ImageView imageView = new ImageView();
        imageView.setImage(imageResource.getImage());
        imageView.setFitWidth(EMBEDDED_MEDIA_RECTANGLE_SIDE);
        imageView.setFitHeight(EMBEDDED_MEDIA_RECTANGLE_SIDE);

        final Rectangle clipRectangle = Clipping.getSquareClip(
                EMBEDDED_MEDIA_RECTANGLE_SIDE,
                EMBEDDED_MEDIA_RECTANGLE_CORNER_RADIUS
        );
        clipRectangle.layoutXProperty().bind(imageView.layoutXProperty());
        clipRectangle.layoutYProperty().bind(imageView.layoutYProperty());
        imageView.setClip(clipRectangle);
        containerPane.getChildren().setAll(imageView);

        Arrays.stream(andThen).forEach(op -> op.accept(imageView));

        setOnOpen(displayScreen, containerPane, mediaUrl);

        return containerPane;
    }

    private void setOnOpen(final MediaDisplaySceen screenToLoad, final Node clickable, String mediaUrl) {
        clickable.setOnMouseClicked(e -> {
            final Pane videoPane = loadMediaScreen(screenToLoad, mediaUrl);
            Stages.stageOf(mediaUrl, videoPane).thenAcceptAsync(Stages::scheduleDisplaying);
        });
    }

    private Pane loadMediaScreen(final MediaDisplaySceen mediaDisplaySceen, final String mediaUrl) {
        final FxmlLoadResult<Pane, MediaScreenController> loadResult = easyFxml.loadNode(
                mediaDisplaySceen,
                Pane.class,
                MediaScreenController.class
        );
        final Pane mediaDisplayScreen = loadResult.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        final MediaScreenController mediaDisplayScreenController = loadResult.getController().getOrElseThrow(
                err -> new IllegalStateException("Could not load the media screen controller !", err)
        );
        mediaDisplayScreenController.handleMedia(mediaUrl);
        return mediaDisplayScreen;
    }

}
