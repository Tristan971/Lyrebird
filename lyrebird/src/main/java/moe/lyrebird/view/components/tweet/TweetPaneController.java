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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import moe.tristan.easyfxml.util.Buttons;
import moe.tristan.easyfxml.util.Nodes;
import moe.lyrebird.model.io.AsyncIO;
import moe.lyrebird.model.twitter.services.interraction.TweetInterractionService;
import moe.lyrebird.view.screens.media.MediaEmbeddingService;
import moe.lyrebird.view.util.BrowserOpeningHyperlink;
import moe.lyrebird.view.util.Clipping;
import moe.lyrebird.view.util.HyperlinkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.model.twitter.services.interraction.Interration.LIKE;
import static moe.lyrebird.model.twitter.services.interraction.Interration.RETWEET;
import static moe.lyrebird.view.assets.ImageResources.BLANK_USER_PROFILE_PICTURE;
import static moe.lyrebird.view.components.tweet.TweetFormatter.time;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetPaneController implements ComponentCellFxmlController<Status> {

    private static final Logger LOG = LoggerFactory.getLogger(TweetPaneController.class);

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
    private TextFlow content;

    @FXML
    private HBox mediaBox;

    @FXML
    private Button likeButton;

    @FXML
    private Button retweetButton;

    @FXML
    private Label retweeterLabel;

    @FXML
    private Label retweeterIdLabel;

    @FXML
    private HBox retweetHbox;

    private final BrowserSupport browserSupport;
    private final TweetInterractionService interractionService;
    private final AsyncIO asyncIO;
    private final MediaEmbeddingService mediaEmbeddingService;

    private Status status;
    private final BooleanProperty isRetweet = new SimpleBooleanProperty(false);

    public TweetPaneController(
            final BrowserSupport browserSupport,
            final TweetInterractionService interractionService,
            final AsyncIO asyncIO,
            final MediaEmbeddingService mediaEmbeddingService
    ) {
        this.browserSupport = browserSupport;
        this.interractionService = interractionService;
        this.asyncIO = asyncIO;
        this.mediaEmbeddingService = mediaEmbeddingService;
    }

    @Override
    public void initialize() {
        authorProfilePicturePane.setClip(makePpClip());
        Nodes.hideAndResizeParentIf(retweetHbox, isRetweet);
        Buttons.setOnClick(likeButton, this::onLike);
        Buttons.setOnClick(retweetButton, this::onRewteet);
        authorProfilePicture.setImage(BLANK_USER_PROFILE_PICTURE.getImage());
    }

    @Override
    public void updateWithValue(final Status newValue) {
        setStatus(newValue);
    }

    private void setStatus(final Status status) {
        if (status == null || this.status == status) {
            return;
        }

        this.status = status;
        this.isRetweet.set(status.isRetweet());

        authorProfilePicture.setImage(BLANK_USER_PROFILE_PICTURE.getImage());
        if (status.isRetweet()) {
            handleRetweet(status);
        } else {
            setStatusDisplay(status);
        }
    }

    private void handleRetweet(final Status status) {
        retweeterLabel.setText(status.getUser().getName());
        retweeterIdLabel.setText("@" + status.getUser().getScreenName());
        setStatusDisplay(status.getRetweetedStatus());
    }

    private void setStatusDisplay(final Status statusToDisplay) {
        author.setText(statusToDisplay.getUser().getName());
        authorId.setText("@" + statusToDisplay.getUser().getScreenName());
        time.setText(time(statusToDisplay));
        loadTextIntoTextFlow(statusToDisplay.getText());
        final String ppUrl = statusToDisplay.getUser().getOriginalProfileImageURLHttps();
        asyncIO.loadImageMiniature(ppUrl, 96.0, 96.0)
               .thenAcceptAsync(authorProfilePicture::setImage, Platform::runLater);
        readMedias(status);
    }

    private void onLike() {
        LOG.debug("Like interraction on status {}", status.getId());
        final CompletableFuture<Status> likeRequest = CompletableFuture.supplyAsync(
                () -> interractionService.interract(status, LIKE)
        );
        likeButton.setDisable(true);
        likeRequest.whenCompleteAsync((res, err) -> likeButton.setDisable(false), Platform::runLater);
    }

    private void onRewteet() {
        LOG.debug("Retweet interraction on status {}", status.getId());
        final CompletableFuture<Status> retweetRequest = CompletableFuture.supplyAsync(
                () -> interractionService.interract(status, RETWEET)
        );
        retweetButton.setDisable(true);
        retweetRequest.whenCompleteAsync((res, err) -> retweetButton.setDisable(false), Platform::runLater);
    }

    private void loadTextIntoTextFlow(final String tweetText) {
        content.getChildren().clear();
        final String strippedText = HyperlinkUtils.stripAllUrls(tweetText);
        final List<String> urlsInText = HyperlinkUtils.findAllUrls(tweetText);

        content.getChildren().add(new Text(strippedText));
        urlsInText.stream()
                  .map(url -> new BrowserOpeningHyperlink(browserSupport::openUrl).withTarget(url))
                  .forEach(content.getChildren()::add);
    }

    private void readMedias(final Status status) {
        final List<Node> embeddingNodes = mediaEmbeddingService.embed(status);
        mediaBox.getChildren().setAll(embeddingNodes);
    }

    private Circle makePpClip() {
        final double clippingRadius = 24.0;
        final Circle ppClip = Clipping.getCircleClip(clippingRadius);
        ppClip.setCenterX(clippingRadius);
        ppClip.setCenterY(clippingRadius);
        return ppClip;
    }

}
