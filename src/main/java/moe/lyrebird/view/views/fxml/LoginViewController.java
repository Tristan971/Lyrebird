package moe.lyrebird.view.views.fxml;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.system.SystemIntegration;
import moe.lyrebird.view.GUIManager;
import moe.lyrebird.view.views.Controller;
import moe.lyrebird.view.views.ErrorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
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
public class LoginViewController implements Controller {
    private final GUIManager guiManager;
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
    public LoginViewController(final ApplicationContext applicationContext) {
        this.twitterHandler = applicationContext.getBean(TwitterHandler.class);
        this.guiManager = applicationContext.getBean(GUIManager.class);
        this.systemIntegration = applicationContext.getBean(SystemIntegration.class);
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
        final Pair<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        log.info("Got authorization URL {}, opening the browser!", tokenUrl.getFirst().toString());
        this.systemIntegration.openBrowser(tokenUrl.getFirst());

        this.loginButton.setDisable(true);
        this.pinCodeField.setVisible(true);
        this.pinCodeButton.setVisible(true);
        this.pinCodeButton.addEventHandler(MOUSE_RELEASED, e -> this.registerPinCode(tokenUrl.getSecond()));
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
                ErrorPane.displayErrorPaneOf("Could not authenticate you!", null);
            }
            this.guiManager.getStages().get(this.getClass()).hide();
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
