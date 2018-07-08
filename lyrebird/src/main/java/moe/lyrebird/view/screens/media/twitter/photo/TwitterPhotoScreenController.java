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

package moe.lyrebird.view.screens.media.twitter.photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.beanmanagement.Selector;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.view.screens.media.MediaScreenController;
import moe.lyrebird.view.screens.media.twitter.TwitterMediaScreen;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterPhotoScreenController extends MediaScreenController {

    @FXML
    private AnchorPane container;

    @FXML
    private ImageView photoImageView;

    private final AsyncIO asyncIO;
    private final StageManager stageManager;

    @Autowired
    public TwitterPhotoScreenController(
            final AsyncIO asyncIO,
            final StageManager stageManager
    ) {
        this.asyncIO = asyncIO;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize() {
        mediaUrlProperty.addListener((o, prev, cur) -> {
            if (cur == null) {
                return;
            }
            asyncIO.loadImageInImageView(cur, photoImageView, this::resizeStageToImageIdealSize);
        });
    }

    private void resizeStageToImageIdealSize(final Image image) {
        final Stage boundStage = stageManager.getMultiple(TwitterMediaScreen.PHOTO, new Selector(this.hashCode()))
                                             .getOrElseThrow(() -> new IllegalStateException(
                                                     "Can not find stage bound to controller" + this
                                             ));
        container.setPrefWidth(image.getWidth());
        container.setPrefHeight(image.getHeight());
        photoImageView.fitHeightProperty().bind(container.heightProperty());
        photoImageView.fitWidthProperty().bind(container.widthProperty());
        boundStage.sizeToScene();
    }

}
