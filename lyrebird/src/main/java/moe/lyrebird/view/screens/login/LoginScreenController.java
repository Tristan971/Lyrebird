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

package moe.lyrebird.view.screens.login;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Buttons;
import io.vavr.Tuple2;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class is responsible for managing the login screen.
 * <p>
 * It is built around a multi-step process to guide the user better.
 * <p>
 * It is also made {@link Lazy} in the case the user is already authenticated and does not want to add another account
 * in the current run of the application.
 * <p>
 * It is made {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE} because there might be multiple login going on at the
 * same time.
 */
@Lazy
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoginScreenController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginScreenController.class);

    @FXML
    private VBox step1Box;

    @FXML
    private Button openLoginUrlButton;

    @FXML
    private VBox step2Box;

    @FXML
    private TextField pinCodeField;

    @FXML
    private Button validatePinCodeButton;

    @FXML
    private VBox step3Box;

    @FXML
    private Label loggedUsernameLabel;

    @FXML
    private Separator separator1;

    @FXML
    private Separator separator2;

    private final BrowserSupport browserSupport;
    private final TwitterHandler twitterHandler;
    private final SessionManager sessionManager;

    public LoginScreenController(
            final BrowserSupport browserSupport,
            final TwitterHandler twitterHandler,
            final SessionManager sessionManager
    ) {
        this.browserSupport = browserSupport;
        this.twitterHandler = twitterHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public void initialize() {
        validatePinCodeButton.setDisable(true);
        Stream.of(step1Box, step2Box, step3Box, separator1, separator2).forEach(node -> setNodeVisibility(node, false));
        uiStep1();

        Buttons.setOnClick(openLoginUrlButton, this::startNewSession);
        this.pinCodeField.textProperty().addListener((o, prev, cur) -> pinCodeTextListener(cur));
    }

    /**
     * Start the first step of the process where the user clicks a link to open an oauth on Twitter's side.
     */
    private void uiStep1() {
        setNodeVisibility(step1Box, true);
    }

    /**
     * Starts the second step of the process where the user must copy an OTP in a field to authenticate the OAuth
     * login.
     */
    private void uiStep2() {
        Stream.of(separator1, step2Box).forEach(node -> setNodeVisibility(node, true));
    }

    /**
     * Ends the process by checking the OTP against the OAuth request and tells the user whether authentication was
     * successful in the end.
     */
    private void uiStep3() {
        Stream.of(separator2, step3Box).forEach(node -> setNodeVisibility(node, true));
    }

    /**
     * Initiates the OAuth request workflow by requesting an OAuth {@link RequestToken} from Twitter.
     */
    private void startNewSession() {
        final Tuple2<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        LOG.info("Got authorization URL {}, opening the browser!", tokenUrl._1);
        browserSupport.openUrl(tokenUrl._1);

        this.openLoginUrlButton.setDisable(true);
        Buttons.setOnClick(validatePinCodeButton, () -> this.registerPinCode(tokenUrl._2));
        uiStep2();
    }

    /**
     * Tries the given OTP (pin code) against Twitter's API.
     *
     * @param requestToken the OAuth request to test the OTP against
     */
    private void registerPinCode(final RequestToken requestToken) {
        final Optional<AccessToken> success = this.twitterHandler.registerAccessToken(
                requestToken,
                this.pinCodeField.getText()
        );
        if (success.isPresent()) {
            sessionManager.addNewSession(this.twitterHandler);
            final AccessToken token = success.get();
            this.loggedUsernameLabel.setText(token.getScreenName());
            validatePinCodeButton.setDisable(true);
            pinCodeField.setDisable(true);
            pinCodeField.setEditable(false);
            uiStep3();
        } else {
            ExceptionHandler.displayExceptionPane(
                    "Authentication Error",
                    "Could not authenticate you!",
                    new Exception("No token could be used.")
            );
        }
    }

    /**
     * Checks whether anything was entered so the user does not mistakenly try to validate the authentication without
     * prior entering of the Twitter OTP.
     *
     * @param currentPin The current pin inside the {@link #pinCodeField}.
     */
    private void pinCodeTextListener(final String currentPin) {
        try {
            Integer.parseInt(currentPin);
            validatePinCodeButton.setDisable(false);
        } catch (final NumberFormatException e) {
            validatePinCodeButton.setDisable(true);
        }
    }

    /**
     * Helper method to manage step box visibility into the containing {@link HBox} by taking advantage of JavaFX layout
     * computation process.
     *
     * @param node    The node targeted
     * @param visible Whether to make it visible or not
     */
    private static void setNodeVisibility(final Node node, final boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
