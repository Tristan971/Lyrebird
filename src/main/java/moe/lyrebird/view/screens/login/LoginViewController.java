package moe.lyrebird.view.screens.login;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Stages;
import io.vavr.Tuple2;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * Created by Tristan on 01/03/2017.
 */
@Component
public class LoginViewController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginViewController.class);

    @FXML
    private Button loginButton;

    @FXML
    private Label loginLabel;

    @FXML
    private TextField pinCodeField;

    @FXML
    private Button pinCodeButton;

    private boolean pinIsValid = false;

    private final BrowserSupport browserSupport;
    private final TwitterHandler twitterHandler;
    private final StageManager stageManager;
    private final SessionManager sessionManager;

    public LoginViewController(
            final BrowserSupport browserSupport,
            final TwitterHandler twitterHandler,
            final StageManager stageManager,
            final SessionManager sessionManager
    ) {
        this.browserSupport = browserSupport;
        this.stageManager = stageManager;
        this.twitterHandler = twitterHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize() {
        this.pinCodeButton.setVisible(false);
        this.pinCodeField.setVisible(false);

        this.pinCodeField.textProperty().addListener(this::pinCodeTextListener);
        this.loginButton.addEventFilter(MOUSE_RELEASED, this::startNewSession);
    }

    @SuppressWarnings("unused")
    private void startNewSession(final Event loginButtonEvent) {
        final Tuple2<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        LOG.info("Got authorization URL {}, opening the browser!", tokenUrl._1);
        browserSupport.openUrl(tokenUrl._1);

        this.loginButton.setDisable(true);
        this.pinCodeField.setVisible(true);
        this.pinCodeButton.addEventHandler(MOUSE_RELEASED, e -> this.registerPinCode(tokenUrl._2));
        this.pinCodeButton.setVisible(true);
    }

    private void registerPinCode(final RequestToken requestToken) {
        if (this.pinIsValid) {
            final Optional<AccessToken> success = this.twitterHandler.registerAccessToken(
                    requestToken,
                    this.pinCodeField.getText()
            );
            if (success.isPresent()) {
                sessionManager.addNewSession(this.twitterHandler);
                final AccessToken token = success.get();
                this.loginButton.setVisible(false);
                this.loginLabel.setText(
                        String.format(
                                "Successfully logged in account @%s!",
                                token.getScreenName()
                        )
                );
            } else {
                ExceptionHandler.displayExceptionPane(
                        "Authentication Error",
                        "Could not authenticate you!",
                        new Exception("No token could be used.")
                );
            }
            this.stageManager.getSingle(Screens.LOGIN_VIEW).peek(Stages::scheduleHiding);
        }
    }

    @SuppressWarnings("unused")
    private void pinCodeTextListener(final ObservableValue<? extends String> o, final String oldVal, final String newVal) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(newVal);
            LoginViewController.this.pinIsValid = true;
        } catch (final NumberFormatException e) {
            LoginViewController.this.pinIsValid = false;
        }
    }
}
