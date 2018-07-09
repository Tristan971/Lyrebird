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

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Tristan on 01/03/2017.
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
        Stream.of(step1Box, step2Box, step3Box, separator1, separator2).forEach(node -> setNodeVisiblity(node, false));
        uiStep1();

        Buttons.setOnClick(openLoginUrlButton, this::startNewSession);
        this.pinCodeField.textProperty().addListener(this::pinCodeTextListener);
    }

    private void uiStep1() {
        setNodeVisiblity(step1Box, true);
    }

    private void uiStep2() {
        Stream.of(separator1, step2Box).forEach(node -> setNodeVisiblity(node, true));
    }

    private void uiStep3() {
        Stream.of(separator2, step3Box).forEach(node -> setNodeVisiblity(node, true));
    }

    private void startNewSession() {
        final Tuple2<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        LOG.info("Got authorization URL {}, opening the browser!", tokenUrl._1);
        browserSupport.openUrl(tokenUrl._1);

        this.openLoginUrlButton.setDisable(true);
        Buttons.setOnClick(validatePinCodeButton, () -> this.registerPinCode(tokenUrl._2));
        uiStep2();
    }

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

    @SuppressWarnings("unused")
    private void pinCodeTextListener(final ObservableValue<? extends String> o, final String oldVal, final String newVal) {
        try {
            Integer.parseInt(newVal);
            validatePinCodeButton.setDisable(false);
        } catch (final NumberFormatException e) {
            validatePinCodeButton.setDisable(true);
        }
    }

    private void setNodeVisiblity(final Node node, final boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
