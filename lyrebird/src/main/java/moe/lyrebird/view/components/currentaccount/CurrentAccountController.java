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

package moe.lyrebird.view.components.currentaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.util.Stages;
import io.vavr.control.Try;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.user.UserDetailsService;
import moe.lyrebird.view.screens.Screens;
import moe.lyrebird.view.util.Clipping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.view.assets.ImageResources.ADD_USER_PROFILE_PICTURE;

@Component
public class CurrentAccountController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    @FXML
    private ImageView userProfilePicture;

    @FXML
    private Label userScreenName;

    private final AsyncIO asyncIO;
    private final EasyFxml easyFxml;
    private final SessionManager sessionManager;
    private final UserDetailsService userDetailsService;

    @Autowired
    public CurrentAccountController(
            final SessionManager sessionManager,
            final AsyncIO asyncIO,
            final EasyFxml easyFxml,
            final UserDetailsService userDetailsService
    ) {
        this.sessionManager = sessionManager;
        this.asyncIO = asyncIO;
        this.easyFxml = easyFxml;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void initialize() {
        userProfilePicture.setClip(makePpClip());
        userProfilePicture.setImage(ADD_USER_PROFILE_PICTURE.getImage());
        userProfilePicture.setOnMouseClicked(e -> handleClickOnProfile());
        bindUsername();
        bindProfilePicture();
    }

    private void bindUsername() {
        this.userScreenName.textProperty().bind(sessionManager.currentSessionUsernameProperty());
    }

    private void bindProfilePicture() {
        sessionManager.currentSessionProperty().addListener(
                (observable, oldValue, newValue) -> this.userChanged(newValue)
        );
        this.userChanged(sessionManager.currentSessionProperty().getValue());
    }

    private void userChanged(final Session newValue) {
        CompletableFuture.runAsync(() -> loadAndSetUserAvatar(newValue.getTwitterUser()));
    }

    private void handleClickOnProfile() {
        LOG.debug("Clicked on current session profile picture");
        if (sessionManager.currentSessionProperty().getValue() == null) {
            LOG.debug("Disconnected. Consider it as a request for new session!");
            handleNewSessionRequest();
        } else {
            LOG.debug("Connected. Consider it as a request to see personal profile.");
            loadDetailsForCurrentUser();
        }
    }

    private void handleNewSessionRequest() {
        easyFxml.loadNode(Screens.LOGIN_VIEW)
                .getNode()
                .map(loginScreen -> Stages.stageOf("Add new account", loginScreen))
                .andThen(Stages::scheduleDisplaying);
    }

    private void loadDetailsForCurrentUser() {
        sessionManager.currentSessionProperty()
                      .getValue()
                      .getTwitterUser()
                      .onSuccess(userDetailsService::openUserDetails);
    }

    private void loadAndSetUserAvatar(final Try<User> user) {
        user.map(User::getOriginalProfileImageURLHttps)
            .map(imageUrl -> asyncIO.loadImageMiniature(imageUrl, 128.0, 128.0))
            .onSuccess(loadRequest -> loadRequest.thenAcceptAsync(userProfilePicture::setImage, Platform::runLater));
    }

    private Circle makePpClip() {
        final double clippingRadius = 32.0;
        final Circle circle = Clipping.getCircleClip(clippingRadius);
        circle.setCenterX(clippingRadius);
        circle.setCenterY(clippingRadius);
        return circle;
    }

}
