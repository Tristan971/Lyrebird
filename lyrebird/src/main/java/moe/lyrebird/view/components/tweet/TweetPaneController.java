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

package moe.lyrebird.view.components.tweet;

import static moe.lyrebird.view.assets.ImageResources.GENERAL_USER_AVATAR_DARK;
import static moe.lyrebird.view.components.FxComponent.TWEET_INTERACTION_BOX;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.user.UserDetailsService;
import moe.lyrebird.view.components.FxComponent;
import moe.lyrebird.view.components.cells.TweetListCell;
import moe.lyrebird.view.screens.media.MediaEmbeddingService;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.util.Clipping;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Nodes;
import twitter4a.Status;

/**
 * Displays a single tweet ({@link Status} in Twitter4J).
 *
 * @see TweetListCell
 * @see NewTweetController
 */
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetPaneController implements ComponentCellFxmlController<Status> {

    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    @FXML
    private Label author;

    @FXML
    private Label authorId;

    @FXML
    private Label time;

    @FXML
    private ImageView authorProfilePicture;

    @FXML
    private Pane authorProfilePicturePane;

    @FXML
    private BorderPane content;

    @FXML
    private HBox mediaBox;

    @FXML
    private Label retweeterLabel;

    @FXML
    private Label retweeterIdLabel;

    @FXML
    private HBox retweetHbox;

    @FXML
    private BorderPane interactionBox;

    private final AsyncIO asyncIO;
    private final MediaEmbeddingService mediaEmbeddingService;
    private final UserDetailsService userDetailsService;
    private final EasyFxml easyFxml;

    private final Property<Status> currentStatus;
    private final BooleanProperty isRetweet = new SimpleBooleanProperty(false);
    private final BooleanProperty embeddedProperty = new SimpleBooleanProperty(false);

    public TweetPaneController(
            final AsyncIO asyncIO,
            final MediaEmbeddingService mediaEmbeddingService,
            final UserDetailsService userDetailsService,
            final EasyFxml easyFxml
    ) {
        this.asyncIO = asyncIO;
        this.mediaEmbeddingService = mediaEmbeddingService;
        this.userDetailsService = userDetailsService;
        this.easyFxml = easyFxml;
        this.currentStatus = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        authorProfilePicturePane.setClip(Clipping.getCircleClip(24.0));
        Nodes.hideAndResizeParentIf(retweetHbox, isRetweet);
        authorProfilePicture.setImage(GENERAL_USER_AVATAR_DARK.getImage());
        setUpInteractionButtons();
        mediaBox.setManaged(false);
        mediaBox.setVisible(false);
    }

    /**
     * @return whether this tweet is displayed in an embedding node (e.g. for replies)
     */
    public BooleanProperty embeddedPropertyProperty() {
        return embeddedProperty;
    }

    private void setUpInteractionButtons() {
        interactionBox.visibleProperty().bind(embeddedProperty.not());
        interactionBox.managedProperty().bind(embeddedProperty.not());

        easyFxml.loadNode(TWEET_INTERACTION_BOX, Pane.class, TweetInteractionPaneController.class)
                .afterControllerLoaded(tipc -> tipc.targetStatusProperty().bind(currentStatus))
                .getNode()
                .recover(ExceptionHandler::fromThrowable)
                .onSuccess(interactionBox::setCenter);
    }

    /**
     * @param newValue The status to prepare displaying for.
     */
    @Override
    public void updateWithValue(final Status newValue) {
        if (newValue == null || this.currentStatus.getValue() == newValue) {
            return;
        }
        this.currentStatus.setValue(newValue);

        this.isRetweet.set(currentStatus.getValue().isRetweet());

        authorProfilePicture.setImage(GENERAL_USER_AVATAR_DARK.getImage());
        if (currentStatus.getValue().isRetweet()) {
            handleRetweet(currentStatus.getValue());
        } else {
            setStatusDisplay(currentStatus.getValue());
        }
    }

    /**
     * Handles retweets so as to add retweet info at the top and then display the actual underlying status.
     */
    private void handleRetweet(final Status status) {
        retweeterLabel.setText(status.getUser().getName());
        retweeterIdLabel.setText("@" + status.getUser().getScreenName());
        setStatusDisplay(status.getRetweetedStatus());
    }

    /**
     * @param statusToDisplay The status to fill user readable information from.
     */
    private void setStatusDisplay(final Status statusToDisplay) {
        author.setText(statusToDisplay.getUser().getName());
        authorId.setText("@" + statusToDisplay.getUser().getScreenName());
        time.setText(PRETTY_TIME.format(statusToDisplay.getCreatedAt()));
        loadTextIntoTextFlow(statusToDisplay);
        final String ppUrl = statusToDisplay.getUser().getOriginalProfileImageURLHttps();
        asyncIO.loadImageMiniature(ppUrl, 96.0, 96.0)
               .thenAcceptAsync(authorProfilePicture::setImage, Platform::runLater);
        authorProfilePicture.setOnMouseClicked(e -> userDetailsService.openUserDetails(statusToDisplay.getUser()));
        readMedias(currentStatus.getValue());
    }

    /**
     * Pre-formats a status' text content.
     *
     * @param status The status whose content we have to format
     */
    private void loadTextIntoTextFlow(final Status status) {
        content.getChildren().clear();

        final FxmlLoadResult<Pane, TweetContentPaneController> result = easyFxml.loadNode(
                FxComponent.TWEET_CONTENT_PANE,
                Pane.class,
                TweetContentPaneController.class
        );

        result.afterControllerLoaded(tcpc -> tcpc.setStatusProp(status))
              .getNode()
              .recover(ExceptionHandler::fromThrowable)
              .andThen(content::setCenter);
    }

    /**
     * Manages extracting and previewing the medias embedded in this tweet.
     *
     * @param status The status for which to manage the embedded media.
     */
    private void readMedias(final Status status) {
        final List<Node> embeddingNodes = mediaEmbeddingService.embed(status);
        mediaBox.setManaged(!embeddingNodes.isEmpty());
        mediaBox.setVisible(!embeddingNodes.isEmpty());
        mediaBox.getChildren().setAll(embeddingNodes);
    }

}
