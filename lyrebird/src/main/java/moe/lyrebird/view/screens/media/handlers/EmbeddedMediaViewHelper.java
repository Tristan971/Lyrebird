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

package moe.lyrebird.view.screens.media.handlers;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.Screen;
import moe.lyrebird.view.screens.media.MediaEmbeddingService;
import moe.lyrebird.view.screens.media.display.MediaDisplayScreen;
import moe.lyrebird.view.screens.media.display.MediaScreenController;
import moe.lyrebird.view.viewmodel.Clipping;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.function.Consumer;

import static moe.lyrebird.view.screens.media.MediaEmbeddingService.EMBEDDED_MEDIA_RECTANGLE_CORNER_RADIUS;
import static moe.lyrebird.view.screens.media.MediaEmbeddingService.EMBEDDED_MEDIA_RECTANGLE_SIDE;

/**
 * This helper class offers basic common setup for creation and management of the previews. (i.e. creation of the
 * miniature, opening of the detailed view etc.)
 *
 * @see MediaEmbeddingService
 * @see moe.lyrebird.view.screens.media.handlers
 */
@Component
public class EmbeddedMediaViewHelper {

    private final EasyFxml easyFxml;

    public EmbeddedMediaViewHelper(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    /**
     * @param displayScreen The screen to open on click on the miniature
     * @param imageResource The image to display as miniature
     * @param mediaUrl      The media that will be displayed in the detailed screen on click
     * @param andThen       Possible post-processing on the view (for async load purposes)
     *
     * @return A media preview {@link ImageView} with given parameters.
     */
    @SafeVarargs
    public final Pane makeWrapperWithIcon(
            final MediaDisplayScreen displayScreen,
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

    /**
     * Binds the click on the preview to opening of the detailed view.
     *
     * @param screenToLoad The {@link MediaDisplayScreen} (subset of {@link Screen}) to open on click
     * @param clickable    The preview node
     * @param mediaUrl     The URL of the media that will be displayed
     */
    private void setOnOpen(final MediaDisplayScreen screenToLoad, final Node clickable, final String mediaUrl) {
        clickable.setOnMouseClicked(e -> {
            final FxmlLoadResult<Pane, MediaScreenController> mediaScreenLoad = loadMediaScreen(screenToLoad, mediaUrl);
            final Pane mediaScreenPane = mediaScreenLoad.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
            final MediaScreenController mediaScreenController = mediaScreenLoad.getController().get();
            Stages.stageOf(mediaUrl, mediaScreenPane).thenAcceptAsync(stage -> {
                mediaScreenController.setStage(stage);
                stage.show();
            }, Platform::runLater);
        });
    }

    /**
     * Opens the given media screen type for a given media.
     *
     * @param mediaDisplayScreen The screen to open this media with
     * @param mediaUrl          The media to display
     *
     * @return The {@link FxmlLoadResult} with preconfigured controller.
     */
    private FxmlLoadResult<Pane, MediaScreenController> loadMediaScreen(
            final MediaDisplayScreen mediaDisplayScreen,
            final String mediaUrl
    ) {
        return easyFxml.loadNode(
                mediaDisplayScreen,
                Pane.class,
                MediaScreenController.class
        ).afterControllerLoaded(msc -> msc.handleMedia(mediaUrl));
    }

}
