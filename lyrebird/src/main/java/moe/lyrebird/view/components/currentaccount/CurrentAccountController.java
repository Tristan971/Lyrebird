package moe.lyrebird.view.components.currentaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.util.Stages;
import io.vavr.control.Try;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.view.CachedDataService;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.view.components.ImageResources.ADD_USER_PROFILE_PICTURE;

@Component
public class CurrentAccountController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    public ImageView userProfilePicture;

    public Label userScreenName;

    private final SessionManager sessionManager;
    private final CachedDataService cachedDataService;
    private final EasyFxml easyFxml;

    @Autowired
    public CurrentAccountController(
            final SessionManager sessionManager,
            final CachedDataService cachedDataService,
            final EasyFxml easyFxml
    ) {
        this.sessionManager = sessionManager;
        this.cachedDataService = cachedDataService;
        this.easyFxml = easyFxml;
    }

    @Override
    public void initialize() {
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
        }
    }

    private void handleNewSessionRequest() {
        easyFxml.loadNode(Screens.LOGIN_VIEW)
                .getNode()
                .map(loginScreen -> Stages.stageOf("Add new account", loginScreen))
                .andThen(Stages::scheduleDisplaying);
    }

    private void loadAndSetUserAvatar(final Try<User> user) {
        user.map(cachedDataService::userProfileImage)
            .onSuccess(userProfilePicture::setImage)
            .onFailure(err -> LOG.error(
                    "Error getting profile picture for current user!",
                    err
            ));
    }

}
