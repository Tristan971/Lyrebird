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

import static moe.lyrebird.view.assets.ImageResources.CONTROLBAR_ADD_USER;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.user.UserDetailsService;
import moe.lyrebird.view.components.controlbar.ControlBarComponent;
import moe.lyrebird.view.screens.login.LoginScreenComponent;
import moe.lyrebird.view.screens.user.UserScreenComponent;
import moe.lyrebird.view.viewmodel.javafx.Clipping;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.util.Stages;

import io.vavr.control.Try;
import twitter4j.User;

/**
 * This component is loaded at the top of the {@link ControlBarComponent} and serves as a very basic preview for who is the current user that will be used to
 * perform all the actions requested.
 *
 * @see SessionManager
 */
@Component
public class CurrentAccountController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    @FXML
    private ImageView userProfilePicture;

    @FXML
    private Circle userProfilePictureBorder;

    @FXML
    private Label userScreenName;

    private final AsyncIO asyncIO;
    private final EasyFxml easyFxml;
    private final SessionManager sessionManager;
    private final UserDetailsService userDetailsService;
    private final LoginScreenComponent loginScreenComponent;

    @Autowired
    public CurrentAccountController(
            SessionManager sessionManager,
            AsyncIO asyncIO,
            EasyFxml easyFxml,
            UserDetailsService userDetailsService,
            LoginScreenComponent loginScreenComponent
    ) {
        this.sessionManager = sessionManager;
        this.asyncIO = asyncIO;
        this.easyFxml = easyFxml;
        this.userDetailsService = userDetailsService;
        this.loginScreenComponent = loginScreenComponent;
    }

    @Override
    public void initialize() {
        userProfilePictureBorder.visibleProperty().bind(sessionManager.isLoggedInProperty());
        userProfilePicture.setClip(Clipping.getCircleClip(32.0));
        userProfilePicture.setImage(CONTROLBAR_ADD_USER.getImage());
        userProfilePicture.setOnMouseClicked(e -> handleClickOnProfile());
        bindUsername();
        bindProfilePicture();
    }

    /**
     * Binds the displayed username to the one resolved by the {@link SessionManager}.
     */
    private void bindUsername() {
        this.userScreenName.textProperty().bind(sessionManager.currentSessionUsernameProperty());
    }

    /**
     * Asynchronously binds the displayed profile picture to the one resolved for the user in the current session via calling {@link SessionManager}.
     */
    private void bindProfilePicture() {
        sessionManager.currentSessionProperty().addListener(
                (observable, oldValue, newValue) -> this.userChanged(newValue)
        );
        this.userChanged(sessionManager.currentSessionProperty().getValue());
    }

    /**
     * Handles the change of a user either from none (unlogged) or to another one if the user has multiple accounts set up.
     *
     * @param newValue The newly selected user for usage.
     */
    private void userChanged(final Session newValue) {
        CompletableFuture.runAsync(() -> loadAndSetUserAvatar(newValue.getTwitterUser()));
    }

    /**
     * Handles clicks on the user profile picture. It either relates to adding an account (in unlogged state) or to displaying the current user's detailed
     * view.
     */
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

    /**
     * Called when the user requests the adding of a new account.
     */
    private void handleNewSessionRequest() {
        easyFxml.load(loginScreenComponent)
                .getNode()
                .map(loginScreen -> Stages.stageOf("Add new account", loginScreen))
                .andThen(Stages::scheduleDisplaying);
    }

    /**
     * Called when the user requests the displaying of the current user's detailed view.
     *
     * @see UserScreenComponent
     */
    private void loadDetailsForCurrentUser() {
        sessionManager.currentSessionProperty()
                      .getValue()
                      .getTwitterUser()
                      .onSuccess(userDetailsService::openUserDetails);
    }

    /**
     * Asynchronous load and set of the user profile picture.
     *
     * @param user The user with which to work, mostly because we do not keep a reference to it inside this class.
     */
    private void loadAndSetUserAvatar(final Try<User> user) {
        user.map(User::getOriginalProfileImageURLHttps)
            .map(imageUrl -> asyncIO.loadImageMiniature(imageUrl, 128.0, 128.0))
            .onSuccess(loadRequest -> loadRequest.thenAcceptAsync(userProfilePicture::setImage, Platform::runLater));
    }

}
