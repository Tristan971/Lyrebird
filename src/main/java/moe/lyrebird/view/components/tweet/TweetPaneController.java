package moe.lyrebird.view.components.tweet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetPaneController implements FxmlController {

    @FXML
    private Label author;

    @FXML
    private Label content;

    private Status status;

    @Override
    public void initialize() {
        // status not yet available !
    }

    public void setStatus(final Status status) {
        Platform.runLater(() -> {
            author.setText(status.getUser().getName() + "("+ status.getUser().getId() +")");
            content.setText(status.getText());
            this.status = status;
        });
    }
}
