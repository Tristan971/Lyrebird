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
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.services.interraction.TwitterInterractionService;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.components.Components;
import moe.lyrebird.view.components.usertimeline.UserTimelineController;
import moe.lyrebird.view.util.Clipping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Relationship;
import twitter4j.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.function.Function;

import static moe.lyrebird.model.twitter.services.interraction.UserInterraction.FOLLOW;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class UserViewController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(UserViewController.class);

    private static final String FOLLOW_BTN_TEXT = "Follow";
    private static final String UNFOLLOW_BTN_TEXT = "Unfollow";
    private static final String YOURSELF_BTN_TEXT = "It's you !";

    @FXML
    private VBox container;

    @FXML
    private VBox userDetailsVBox;

    @FXML
    private ImageView userBanner;

    @FXML
    private ImageView userProfilePictureImageView;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Button followButton;

    @FXML
    private Label userTweetCount;

    @FXML
    private Label userFollowingCount;

    @FXML
    private Label userFollowerCount;

    @FXML
    private Label userDescription;

    @FXML
    private Label userFriendshipStatus;

    @FXML
    private Label userLocation;

    @FXML
    private Separator userLocationWebsiteSeparator;

    @FXML
    private Label userWebsite;

    @FXML
    private Label userCreationDate;

    private final EasyFxml easyFxml;
    private final AsyncIO asyncIO;
    private final SessionManager sessionManager;
    private final TwitterInterractionService interractionService;

    private final Property<User> targetUser;

    @Autowired
    public UserViewController(
            final EasyFxml easyFxml,
            final AsyncIO asyncIO,
            final SessionManager sessionManager,
            final TwitterInterractionService interractionService
    ) {
        this.easyFxml = easyFxml;
        this.asyncIO = asyncIO;
        this.sessionManager = sessionManager;
        this.interractionService = interractionService;
        this.targetUser = new SimpleObjectProperty<>(null);
    }

    public Property<User> targetUserProperty() {
        return targetUser;
    }

    @Override
    public void initialize() {
        followButton.setDisable(true);
        userBanner.fitWidthProperty().bind(userDetailsVBox.widthProperty());
        userBanner.fitHeightProperty().bind(userDetailsVBox.heightProperty());
        userBanner.setPreserveRatio(false);
        userBanner.setImage(ImageResources.BACKGROUND_DARK_1PX.getImage());

        userProfilePictureImageView.setImage(ImageResources.BLANK_USER_PROFILE_PICTURE_LIGHT.getImage());
        final Rectangle profilePictureClip = Clipping.getSquareClip(290.0, 50.0);

        userProfilePictureImageView.setClip(profilePictureClip);
        if (targetUser.getValue() == null) {
            this.targetUser.addListener((o, prev, cur) -> displayTargetUser());
        } else {
            displayTargetUser();
        }
    }

    private void displayTargetUser() {
        this.fillTextData();
        this.asyncLoadImages();
        this.setupFollowButton();
        this.loadTargetUserTimeline();
    }

    private void fillTextData() {
        final User user = targetUser.getValue();
        userNameLabel.setText(user.getName());
        userIdLabel.setText("@" + user.getScreenName());

        userDescription.setText(user.getDescription());

        userTweetCount.setText(Integer.toString(user.getStatusesCount()));
        userFollowingCount.setText(Integer.toString(user.getFriendsCount()));
        userFollowerCount.setText(Integer.toString(user.getFollowersCount()));

        userCreationDate.setText(user.getCreatedAt().toString());

        final String userLocationText = user.getLocation();
        if (userLocationText != null) {
            userLocation.setText(userLocationText);
        } else {
            userLocation.setVisible(false);
            userLocation.setManaged(false);
        }

        final String userWebsiteText = user.getURL();
        if (userWebsiteText != null) {
            userWebsite.setText(userWebsiteText);
        } else {
            userWebsite.setVisible(false);
            userWebsite.setManaged(false);
        }

        if (userLocationText == null || userWebsiteText == null) {
            userLocationWebsiteSeparator.setVisible(false);
            userLocationWebsiteSeparator.setManaged(false);
        }

    }

    private void asyncLoadImages() {
        final User user = targetUser.getValue();
        asyncIO.loadImage(user.getOriginalProfileImageURLHttps())
               .thenAcceptAsync(userProfilePictureImageView::setImage, Platform::runLater);

        final String bannerUrl = user.getProfileBannerURL();
        if (bannerUrl == null) {
            LOG.debug("No profile banner. Keep background color pixel instead.");
        } else {
            asyncIO.loadImage(bannerUrl)
                   .thenAcceptAsync(userBanner::setImage, Platform::runLater);
        }
    }

    private void setupFollowButton() {
        final User user = targetUser.getValue();
        followButton.setOnAction(e -> {
            interractionService.interract(user, FOLLOW);
            updateFollowButtonText();
        });
        updateFollowButtonText();
    }

    private void updateFollowButtonText() {
        final User user = targetUser.getValue();
        followButton.setText(interractionService.notYetFollowed(user) ? FOLLOW_BTN_TEXT : UNFOLLOW_BTN_TEXT);
        sessionManager.currentSessionProperty().getValue().getTwitterUser().onSuccess(me -> {
            if (me.getId() == user.getId()) {
                followButton.setText(YOURSELF_BTN_TEXT);
                followButton.setDisable(true);
            } else {
                followButton.setDisable(false);
            }
            updateFriendshipStatus();
        });
    }

    private void updateFriendshipStatus() {
        final Relationship relationship = getRelationship();

        String relStatusText = null;
        if (relationship.isTargetFollowingSource()) {
            if (relationship.isTargetFollowedBySource()) {
                relStatusText = "You follow each other!";
            } else {
                relStatusText = "Follows you.";
            }
        }

        if (relStatusText == null) {
            userFriendshipStatus.setVisible(false);
            userFriendshipStatus.setManaged(false);
        } else {
            userFriendshipStatus.setText(relStatusText);
            userFriendshipStatus.setVisible(true);
            userFriendshipStatus.setManaged(true);
        }
    }

    private Relationship getRelationship() {
        return sessionManager.doWithCurrentTwitter(
                twitter -> sessionManager.currentSessionProperty()
                                         .getValue()
                                         .getTwitterUser()
                                         .mapTry(us -> twitter.showFriendship(
                                                 us.getId(),
                                                 targetUser.getValue().getId()
                                         )).get()
        ).getOrElseThrow((Function<? super Throwable, IllegalStateException>) IllegalStateException::new);
    }

    private void loadTargetUserTimeline() {
        final User user = targetUser.getValue();
        final FxmlLoadResult<Pane, UserTimelineController> userTimelineLoad = easyFxml.loadNode(
                Components.USER_TIMELINE,
                Pane.class,
                UserTimelineController.class
        );

        userTimelineLoad.getController()
                        .onFailure(err -> LOG.error("Could not load user timeline!", err))
                        .onSuccess(utc -> utc.setTargetUser(user));

        userTimelineLoad.getNode()
                        .recover(ExceptionHandler::fromThrowable)
                        .onSuccess(userDetailsPane -> {
                            VBox.setVgrow(userDetailsPane, Priority.ALWAYS);
                            container.getChildren().add(userDetailsPane);
                        });
    }

}
