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

package moe.lyrebird.view.screens.media.display.twitter.photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.view.components.ImageResources;
import moe.lyrebird.view.screens.media.MediaScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterPhotoScreenController extends MediaScreenController {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterPhotoScreenController.class);

    @FXML
    private AnchorPane container;

    @FXML
    private ImageView photoImageView;

    private final AsyncIO asyncIO;

    private final Property<Image> imageProp = new SimpleObjectProperty<>(ImageResources.LOADING_REMOTE.getImage());

    @Autowired
    public TwitterPhotoScreenController(final AsyncIO asyncIO) {
        this.asyncIO = asyncIO;
    }

    @Override
    public void initialize() {
        photoImageView.imageProperty().bind(imageProp);
    }

    @Override
    public void handleMedia(String mediaUrl) {
        asyncIO.loadImageAndThen(mediaUrl, image -> {
            LOG.debug("Loading image from {} inside image viewer {}", mediaUrl, this);
            imageProp.setValue(image);
            autosizeStage(image);
        });
    }

    private void autosizeStage(final Image image) {
        LOG.debug("Resizing stage to fit size of image {}", image);
        container.setPrefWidth(image.getWidth());
        container.setPrefHeight(image.getHeight());
        photoImageView.fitHeightProperty().bind(container.heightProperty());
        photoImageView.fitWidthProperty().bind(container.widthProperty());
    }

}
