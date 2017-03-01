package moe.lyrebird.view.views.fxml;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.twitter4j.TwitterHandler;
import moe.lyrebird.view.views.ErrorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * Created by Tristan on 01/03/2017.
 */
@SuppressWarnings("unused")
@Component
@Slf4j
public class LoginViewController {
    private final TwitterHandler twitterHandler;
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
    }
    
    public void initialize() {
        this.pinCodeButton.setVisible(false);
        this.pinCodeField.setVisible(false);
        this.pinCodeField.addEventHandler(Event.ANY, this::pinCodeTextListener);
        
        this.loginButton.addEventFilter(MOUSE_RELEASED, this::startNewSession);
    }
    
    private void startNewSession(final Event loginButtonEvent) {
        final Pair<URL, RequestToken> tokenUrl = this.twitterHandler.newSession();
        log.info("Got authorization URL {}, opening the browser!", tokenUrl.getFirst().toString());
        try {
            Desktop.getDesktop().browse(tokenUrl.getFirst().toURI());

            this.loginButton.setDisable(true);
            this.pinCodeField.setVisible(true);
            this.pinCodeButton.setVisible(true);
            this.pinCodeButton.addEventHandler(MOUSE_RELEASED, e -> this.registerPinCode(tokenUrl.getSecond()));
            
        } catch (final URISyntaxException e) {
            log.info("Bad URL returned! [{}]", tokenUrl.getFirst().toString());
            ErrorPane.displayErrorPaneOf("Bad URL returned!" + tokenUrl.getFirst().toString(), e);
        } catch (IOException e) {
            log.info("Could not get a handle to the OS's browser!");
            ErrorPane.displayErrorPaneOf(
                    "Browser unavailable!\n" +
                            "We couldn't open your default browser, so you need" +
                            "to access the following URL "+tokenUrl.getFirst().toString()+
                            "manually with your preferred browser to get the pin code.",
                    e
            );
        }
    }
    
    private void registerPinCode(final RequestToken requestToken) {
        if (this.pinIsValid) {
            final boolean success = this.twitterHandler.registerAccessToken(
                    requestToken,
                    this.pinCodeField.getText()
            );
            if (!success) {
                ErrorPane.displayErrorPaneOf("Could not authenticate you!", null);
            }
        }
    }
    
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
