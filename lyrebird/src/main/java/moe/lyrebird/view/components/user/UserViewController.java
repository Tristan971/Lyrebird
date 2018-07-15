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

package moe.lyrebird.view.components.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.observables.UserTimeline;
import moe.lyrebird.model.twitter.services.interraction.TwitterInterractionService;
import twitter4j.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import static moe.lyrebird.model.twitter.services.interraction.UserInterraction.FOLLOW;

@Component
public class UserViewController implements FxmlController {

    @FXML
    private ImageView userProfilePictureImageView;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Button followSwitchButton;

    @FXML
    private AnchorPane userLastTweetsTimelineContainer;

    private final EasyFxml easyFxml;
    private final AsyncIO asyncIO;
    private final UserTimeline userTimeline;
    private final TwitterInterractionService interractionService;

    private final Property<User> targetUser;

    @Autowired
    public UserViewController(
            final EasyFxml easyFxml,
            final AsyncIO asyncIO,
            final UserTimeline userTimeline,
            final TwitterInterractionService interractionService
    ) {
        this.easyFxml = easyFxml;
        this.asyncIO = asyncIO;
        this.userTimeline = userTimeline;
        this.interractionService = interractionService;
        this.targetUser = new ReadOnlyObjectWrapper<>();
        this.targetUser.bindBidirectional(userTimeline.targetUserProperty());
        this.targetUser.addListener((o, prev, cur) -> handleTargetUserSet());
    }

    @Override
    public void initialize() {
    }

    public void setTargetUser(final User targetUser) {
        this.targetUser.setValue(targetUser);
    }

    private void handleTargetUserSet() {
        userNameLabel.setText(targetUser.getValue().getName());
        userIdLabel.setText("@" + targetUser.getValue().getScreenName());
        followSwitchButton.setOnAction(e -> interractionService.interract(targetUser.getValue(), FOLLOW));
        asyncIO.loadImage(targetUser.getValue().getOriginalProfileImageURLHttps())
               .thenAcceptAsync(userProfilePictureImageView::setImage, Platform::runLater);
    }

}
