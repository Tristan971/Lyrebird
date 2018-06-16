package moe.lyrebird.view.components.currentaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.view.CachedDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.concurrent.CompletableFuture;

@Component
public class CurrentAccountController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    @FXML
    private ImageView userProfilePicture;

    @FXML
    private Label userScreenName;

    private final SessionManager sessionManager;
    private final CachedDataService cachedDataService;

    @Autowired
    public CurrentAccountController(
            final SessionManager sessionManager,
            final CachedDataService cachedDataService
    ) {
        this.sessionManager = sessionManager;
        this.cachedDataService = cachedDataService;
    }

    @Override
    public void initialize() {
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
        CompletableFuture.runAsync(
                () -> newValue.getTwitterUser()
                              .map(cachedDataService::userProfileImage)
                              .onSuccess(userProfilePicture::setImage)
                              .onFailure(err -> LOG.error(
                                      "Error getting PP for current session!",
                                      err
                              ))
        );
    }

}
