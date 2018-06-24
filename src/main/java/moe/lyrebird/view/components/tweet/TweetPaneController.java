package moe.lyrebird.view.components.tweet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import moe.tristan.easyfxml.util.Nodes;
import moe.lyrebird.model.twitter.services.TweetInterractionService;
import moe.lyrebird.view.CachedDataService;
import twitter4j.Status;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.view.components.ImageResources.BLANK_USER_PROFILE_PICTURE;
import static moe.lyrebird.view.components.tweet.TweetFormatter.tweetContent;
import static moe.lyrebird.view.components.tweet.TweetFormatter.username;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetPaneController implements ComponentCellFxmlController<Status> {

    @FXML
    private Label author;

    @FXML
    private ImageView authorProfilePicture;

    @FXML
    private TextFlow content;

    @FXML
    private HBox toolbar;

    @FXML
    private Button likeButton;

    @FXML
    private Button retweetButton;

    private final TweetInterractionService interractionService;
    private final CachedDataService cachedDataService;

    private Status status;
    public final BooleanProperty selected = new SimpleBooleanProperty(false);

    public TweetPaneController(
            final TweetInterractionService interractionService,
            final CachedDataService cachedDataService
    ) {
        this.interractionService = interractionService;
        this.cachedDataService = cachedDataService;
    }

    @Override
    public void initialize() {
        Nodes.hideAndResizeParentIf(toolbar, selected);
        likeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedEvent -> onLike());
        retweetButton.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedEvent -> onRewteet());
        authorProfilePicture.setImage(BLANK_USER_PROFILE_PICTURE.getImage());
    }

    @Override
    public void updateWithValue(final Status newValue) {
        setStatus(newValue);
    }

    @Override
    public void selectedProperty(final BooleanExpression selected) {
        this.selected.bind(selected);
    }

    private void setStatus(final Status status) {
        author.setText(username(status.getUser()));
        content.getChildren().clear();
        content.getChildren().add(new Text(tweetContent(status)));
        this.status = status;
        authorProfilePicture.setImage(BLANK_USER_PROFILE_PICTURE.getImage());
        CompletableFuture.supplyAsync(() -> cachedDataService.userProfileImage(status.getUser()))
                         .thenAccept(authorProfilePicture::setImage);
    }

    private void onLike() {
        interractionService.like(status);
        likeButton.setDisable(true);
    }

    private void onRewteet() {
        retweetButton.setDisable(true);
        interractionService.retweet(status);
    }
}
