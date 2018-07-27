package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.services.TwitterInformationService;
import twitter4a.DirectMessageEvent;
import twitter4a.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMPaneController implements ComponentCellFxmlController<DirectMessageEvent> {

    @FXML
    private ImageView currentUserPpBox;

    @FXML
    private ImageView otherPpBox;

    @FXML
    private Label messageContent;

    private final AsyncIO asyncIO;
    private final TwitterInformationService twitterInformationService;
    private final SessionManager sessionManager;

    private final Property<DirectMessageEvent> currentMessage = new SimpleObjectProperty<>(null);

    @Autowired
    public DMPaneController(
            final SessionManager sessionManager,
            final AsyncIO asyncIO,
            final TwitterInformationService twitterInformationService
    ) {
        this.sessionManager = sessionManager;
        this.asyncIO = asyncIO;
        this.twitterInformationService = twitterInformationService;
    }

    @Override
    public void initialize() {
        textualContent();
        profilePictures();
    }

    @Override
    public void updateWithValue(DirectMessageEvent newValue) {
        this.currentMessage.setValue(newValue);
    }

    private void profilePictures() {
        JavaFxObservable.valuesOf(currentMessage).forEach(messageEvent -> {
            final boolean isSentByMe = sessionManager.isCurrentUser(messageEvent.getSenderId());
            final User sender = twitterInformationService.getUser(messageEvent.getSenderId());
            if (isSentByMe) {
                ppSetupSender(currentUserPpBox, sender);
                ppSetupReceiver(otherPpBox);
            } else {
                ppSetupSender(otherPpBox, sender);
                ppSetupReceiver(currentUserPpBox);
            }
        });
    }

    private void ppSetupSender(final ImageView ppView, final User user) {
        ppView.setVisible(true);
        ppView.setManaged(true);
        asyncIO.loadImageMiniature(user.getProfileImageURLHttps(), 128.0, 128.0)
               .thenAcceptAsync(ppView::setImage, Platform::runLater);
    }

    private void ppSetupReceiver(final ImageView ppView) {
        ppView.setVisible(true);
        ppView.setManaged(true);
    }

    private void textualContent() {
        JavaFxObservable.valuesOf(currentMessage)
                        .map(DirectMessageEvent::getText)
                        .observeOn(JavaFxScheduler.platform())
                        .forEach(messageContent::setText);
    }

}
