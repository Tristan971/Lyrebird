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
import moe.lyrebird.model.twitter.services.CachedTwitterInfoService;
import moe.lyrebird.model.twitter.user.UserDetailsService;
import moe.lyrebird.view.viewmodel.Clipping;
import twitter4a.DirectMessageEvent;
import twitter4a.User;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMPaneController implements ComponentCellFxmlController<DirectMessageEvent> {

    @FXML
    private HBox container;

    @FXML
    private ImageView currentUserPpBox;

    @FXML
    private ImageView otherPpBox;

    @FXML
    private Label messageContent;

    private final AsyncIO asyncIO;
    private final CachedTwitterInfoService cachedTwitterInfoService;
    private final SessionManager sessionManager;
    private final UserDetailsService userDetailsService;

    private final Property<DirectMessageEvent> currentMessage = new SimpleObjectProperty<>(null);

    @Autowired
    public DMPaneController(
            final SessionManager sessionManager,
            final AsyncIO asyncIO,
            final CachedTwitterInfoService cachedTwitterInfoService,
            final UserDetailsService userDetailsService
    ) {
        this.sessionManager = sessionManager;
        this.asyncIO = asyncIO;
        this.cachedTwitterInfoService = cachedTwitterInfoService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void initialize() {
        currentUserPpBox.setClip(Clipping.getCircleClip(24.0));
        otherPpBox.setClip(Clipping.getCircleClip(24.0));
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
            final User sender = cachedTwitterInfoService.getUser(messageEvent.getSenderId());
            if (isSentByMe) {
                container.setAlignment(Pos.TOP_RIGHT);
                ppSetupSender(currentUserPpBox, sender);
                ppSetupReceiver(otherPpBox);
            } else {
                container.setAlignment(Pos.TOP_LEFT);
                ppSetupSender(otherPpBox, sender);
                ppSetupReceiver(currentUserPpBox);
            }
        });
    }

    private void ppSetupSender(final ImageView ppView, final User user) {
        ppView.setVisible(true);
        ppView.setManaged(true);
        ppView.setOnMouseClicked(e -> userDetailsService.openUserDetails(user));
        asyncIO.loadImageMiniature(user.getProfileImageURLHttps(), 128.0, 128.0)
               .thenAcceptAsync(ppView::setImage, Platform::runLater);
    }

    private void ppSetupReceiver(final ImageView ppView) {
        ppView.setVisible(false);
        ppView.setManaged(false);
    }

    private void textualContent() {
        JavaFxObservable.valuesOf(currentMessage)
                        .map(DirectMessageEvent::getText)
                        .observeOn(JavaFxScheduler.platform())
                        .forEach(messageContent::setText);
    }

}
