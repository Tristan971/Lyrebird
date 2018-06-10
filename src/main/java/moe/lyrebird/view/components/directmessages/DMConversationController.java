package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import moe.lyrebird.view.components.cells.DirectMessageListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.DirectMessage;
import twitter4j.User;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.function.Supplier;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMConversationController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(DMConversationController.class);

    @FXML
    private ListView<DirectMessage> messageListView;

    private final DirectMessages directMessages;
    private final Supplier<DirectMessageListCell> directMessageListCellSupplier;

    private User pal = null;
    private final ReadOnlyProperty<User> palProperty;

    public DMConversationController(
            final DirectMessages directMessages,
            final ApplicationContext applicationContext
    ) {
        this.directMessages = directMessages;
        this.palProperty = new ReadOnlyObjectWrapper<>(pal);
        this.directMessageListCellSupplier = () -> applicationContext.getBean(DirectMessageListCell.class);
    }

    @Override
    public void initialize() {
        LOG.debug("Created conversation view for conversing with user!");
        LOG.debug("Schedule displaying of conversation once senderId has been received!");
        palProperty.addListener((palVal, oldVal, newVal) -> deferredPalSetCall(newVal));
        messageListView.setCellFactory(messages -> directMessageListCellSupplier.get());
    }

    public void setPal(User pal) {
        LOG.debug("Mapping DM conversation view {} with senderId {}", this, pal.getScreenName());
        this.pal = pal;
    }

    private void deferredPalSetCall(final User palValue) {
        this.messageListView.getItems().addAll(directMessages.loadedConversations().get(palValue));
    }

}
