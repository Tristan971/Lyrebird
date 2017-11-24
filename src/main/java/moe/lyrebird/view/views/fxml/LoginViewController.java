package moe.lyrebird.view.views.fxml;

import io.vavr.Tuple2;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.system.SystemIntegration;
import moe.lyrebird.view.views.Views;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.StageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.Optional;

import static javafx.event.Event.ANY;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * Created by Tristan on 01/03/2017.
 */
@Component
@Slf4j
public class LoginViewController implements FxmlController {
    private final StageManager stageManager;
    private final TwitterHandler twitterHandler;
    private final SystemIntegration systemIntegration;
    
    @FXML
    private Button loginButton;
    @FXML
    private Label loginLabel;
    @FXML
    private TextField pinCodeField;
    @FXML
    private Button pinCodeButton;
    private boolean pinIsValid = false;
    
    @Autowired
    public LoginViewController(final TwitterHandler twitterHandler, final StageManager stageManager, final SystemIntegration systemIntegration) {
        this.twitterHandler = twitterHandler;
        this.stageManager = stageManager;
        this.systemIntegration = systemIntegration;
    }
    
    @Override
    public void initialize() {
        this.pinCodeButton.setVisible(false);
        this.pinCodeField.setVisible(false);
        
        this.pinCodeField.addEventHandler(ANY, this::pinCodeTextListener);
        this.loginButton.addEventFilter(MOUSE_RELEASED, this::startNewSession);
    }
    
    @SuppressWarnings("unused")
    private void startNewSession(final Event loginButtonEvent) {
        final Tuple2<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        log.info("Got authorization URL {}, opening the browser!", tokenUrl._1.toString());
        this.systemIntegration.openBrowser(tokenUrl._1);

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
                final AccessToken token = success.get();
                this.loginButton.setVisible(false);
                this.loginLabel.setText(
                        String.format(
                                "Successfully logged in account @%s!",
                                token.getScreenName())
                );
            } else {
                ExceptionHandler.displayExceptionPane(
                        "Authentication Error",
                        "Could not authenticate you!",
                        new Exception("No token could be used.")
                );
            }
            this.stageManager.getSingle(Views.LOGIN_VIEW).peek(StageUtils::scheduleHiding);
        }
    }
    
    @SuppressWarnings("unused")
    private void pinCodeTextListener(final Event keyEvent) {
        final String text = LoginViewController.this.pinCodeField.getText();
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(text);
            LoginViewController.this.pinIsValid = true;
        } catch (final NumberFormatException e) {
            LoginViewController.this.pinIsValid = false;
        }
    }
}
