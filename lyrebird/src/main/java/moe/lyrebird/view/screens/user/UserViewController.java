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

package moe.lyrebird.view.screens.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.services.interraction.TwitterInterractionService;
import moe.lyrebird.view.components.Components;
import moe.lyrebird.view.components.usertimeline.UserTimelineController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static moe.lyrebird.model.twitter.services.interraction.UserInterraction.FOLLOW;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class UserViewController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(UserViewController.class);

    @FXML
    private ImageView userProfilePictureImageView;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Button followSwitchButton;

    @FXML
    public VBox container;

    private final EasyFxml easyFxml;
    private final AsyncIO asyncIO;
    private final TwitterInterractionService interractionService;

    private final Property<User> targetUser;

    @Autowired
    public UserViewController(
            final EasyFxml easyFxml,
            final AsyncIO asyncIO,
            final TwitterInterractionService interractionService
    ) {
        this.easyFxml = easyFxml;
        this.asyncIO = asyncIO;
        this.interractionService = interractionService;
        this.targetUser = new SimpleObjectProperty<>(null);
    }

    public Property<User> targetUserProperty() {
        return targetUser;
    }

    @Override
    public void initialize() {
        if (targetUser.getValue() == null) {
            this.targetUser.addListener((o, prev, cur) -> displayTargetUser());
        } else {
            displayTargetUser();
        }
    }

    private void displayTargetUser() {
        userNameLabel.setText(targetUser.getValue().getName());
        userIdLabel.setText("@" + targetUser.getValue().getScreenName());
        followSwitchButton.setOnAction(e -> interractionService.interract(targetUser.getValue(), FOLLOW));
        asyncIO.loadImage(targetUser.getValue().getOriginalProfileImageURLHttps())
               .thenAcceptAsync(userProfilePictureImageView::setImage, Platform::runLater);

        final FxmlLoadResult<Pane, UserTimelineController> userTimelineLoad = easyFxml.loadNode(
                Components.USER_TIMELINE,
                Pane.class,
                UserTimelineController.class
        );

        userTimelineLoad.getController()
                        .onFailure(err -> LOG.error("Could not load user timeline!", err))
                        .onSuccess(utc -> utc.setTargetUser(targetUser.getValue()));

        userTimelineLoad.getNode()
                        .recover(ExceptionHandler::fromThrowable)
                        .onSuccess(userDetailsPane -> {
                            VBox.setVgrow(userDetailsPane, Priority.ALWAYS);
                            container.getChildren().add(userDetailsPane);
                        });
    }

}
