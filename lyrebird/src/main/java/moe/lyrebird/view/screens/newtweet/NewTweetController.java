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

package moe.lyrebird.view.screens.newtweet;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.TwitterMediaExtensionFilter;
import moe.lyrebird.model.twitter.services.NewTweetService;
import moe.lyrebird.view.components.FxComponent;
import moe.lyrebird.view.components.tweet.TweetPaneController;
import moe.lyrebird.view.viewmodel.javafx.Clipping;
import moe.lyrebird.view.viewmodel.javafx.StageAware;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Buttons;

import twitter4a.Status;
import twitter4a.User;
import twitter4a.UserMentionEntity;

/**
 * This controller manages the new tweet view.
 * <p>
 * It is made {@link Lazy} in case the user never uses it.
 * <p>
 * It is also made {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE} in case multiple new tweet screens are open at the
 * same time.
 */
@Lazy
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTweetController implements FxmlController, StageAware {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetController.class);

    private static final double MEDIA_PREVIEW_IMAGE_SIZE = 32.0;

    @FXML
    private BorderPane container;

    @FXML
    private Button sendButton;

    @FXML
    private Button pickMediaButton;

    @FXML
    private HBox mediaPreviewBox;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;

    private final NewTweetService newTweetService;
    private final TwitterMediaExtensionFilter twitterMediaExtensionFilter;
    private final EasyFxml easyFxml;
    private final SessionManager sessionManager;

    private final ListProperty<File> mediasToUpload;
    private final Property<Stage> embeddingStage;
    private final Property<Status> inReplyStatus = new SimpleObjectProperty<>();

    public NewTweetController(
            final NewTweetService newTweetService,
            final TwitterMediaExtensionFilter extensionFilter,
            final EasyFxml easyFxml,
            final SessionManager sessionManager
    ) {
        this.newTweetService = newTweetService;
        this.twitterMediaExtensionFilter = extensionFilter;
        this.easyFxml = easyFxml;
        this.sessionManager = sessionManager;
        this.mediasToUpload = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        this.embeddingStage = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        LOG.debug("New tweet stage ready.");
        enableTweetLengthCheck();
        Buttons.setOnClick(sendButton, this::send);
        Buttons.setOnClick(pickMediaButton, this::openMediaAttachmentsFilePicker);

        final BooleanBinding mediasNotEmpty = mediasToUpload.emptyProperty().not();
        mediaPreviewBox.visibleProperty().bind(mediasNotEmpty);
        mediaPreviewBox.managedProperty().bind(mediasNotEmpty);

        inReplyStatus.addListener((o, prev, cur) -> prefillMentionsForReply());
    }

    /**
     * @param embeddingStage The stage that will be closed on successful publication of the tweet
     */
    @Override
    public void setStage(final Stage embeddingStage) {
        this.embeddingStage.setValue(embeddingStage);
    }

    /**
     * If this tweet is in reply to another, we set proper display of the previous one because it looks good.
     *
     * @param repliedTweet the tweet ({@link Status} to which this is in reply to
     */
    public void setInReplyToTweet(final Status repliedTweet) {
        LOG.debug("Set new tweet stage to embed status : {}", repliedTweet.getId());
        inReplyStatus.setValue(repliedTweet);
        easyFxml.loadNode(FxComponent.TWEET, Pane.class, TweetPaneController.class)
                .afterControllerLoaded(tpc -> {
                    tpc.embeddedPropertyProperty().setValue(true);
                    tpc.updateWithValue(repliedTweet);
                })
                .getNode()
                .recover(ExceptionHandler::fromThrowable)
                .onSuccess(this.container::setTop);
    }

    /**
     * In case this is a reply we pre-fill the content field with the appropriate mentions.
     */
    private void prefillMentionsForReply() {
        final User currentUser = sessionManager.currentSessionProperty().getValue().getTwitterUser().get();

        final Status replied = inReplyStatus.getValue();
        final StringBuilder prefillText = new StringBuilder();
        prefillText.append('@').append(replied.getUser().getScreenName());
        Arrays.stream(replied.getUserMentionEntities())
              .map(UserMentionEntity::getScreenName)
              .filter(username -> !username.equals(currentUser.getScreenName()))
              .forEach(username -> prefillText.append(' ').append('@').append(username));
        prefillText.append(' ');
        final String prefill = prefillText.toString();
        tweetTextArea.setText(prefill);
        tweetTextArea.positionCaret(prefill.length());
    }

    /**
     * Makes the character counter colorful depending on the current amount typed relative to the maximum Twitter
     * allows.
     */
    private void enableTweetLengthCheck() {
        tweetTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            final Color color = Match(newValue.length()).of(
                    Case($(tweetLen -> tweetLen < 250), GREEN),
                    Case($(tweetLen -> tweetLen >= 250 && tweetLen < 280), ORANGE),
                    Case($(tweetLen -> tweetLen > 280), RED),
                    Case($(tweetLen -> tweetLen == 280), BLUE)
            );
            Platform.runLater(() -> {
                charactersLeft.setText(Integer.toString(newValue.length()));
                charactersLeft.setTextFill(color);
            });
        });
    }

    /**
     * Dynamically either sends a normal tweet or a reply depending on whether the controller was used to prepare a
     * "normal" new tweet or a reply (i.e. if there was a value set for {@link #inReplyStatus}.
     */
    private void send() {
        if (inReplyStatus.getValue() == null) {
            sendTweet();
        } else {
            sendReply();
        }
    }

    /**
     * Sends a tweet using the {@link NewTweetService}.
     */
    private void sendTweet() {
        Stream.of(tweetTextArea, sendButton, pickMediaButton).forEach(ctr -> ctr.setDisable(true));
        newTweetService.sendTweet(tweetTextArea.getText(), mediasToUpload)
                       .thenAcceptAsync(status -> {
                           LOG.info("Tweeted status : {} [{}]", status.getId(), status.getText());
                           this.embeddingStage.getValue().hide();
                       }, Platform::runLater);
    }

    /**
     * Sends a reply using the {@link NewTweetService}.
     */
    private void sendReply() {
        final long inReplyToId = inReplyStatus.getValue().getId();
        Stream.of(tweetTextArea, sendButton, pickMediaButton).forEach(ctr -> ctr.setDisable(true));
        newTweetService.sendReply(tweetTextArea.getText(), mediasToUpload, inReplyToId)
                       .thenAcceptAsync(status -> {
                           LOG.info("Tweeted reply to {} : {} [{}]", inReplyToId, status.getId(), status.getText());
                           this.embeddingStage.getValue().hide();
                       }, Platform::runLater);
    }

    /**
     * Opens a file picker for choosing attachments for the currently made tweet and registers the asynchronous choice
     * result in {@link #mediasToUpload}.
     */
    private void openMediaAttachmentsFilePicker() {
        pickMediaButton.setDisable(true);
        this.openFileChooserForMedia()
            .thenAcceptAsync(
                    this::mediaFilesChosen,
                    Platform::runLater
            );
    }

    /**
     * Opens a {@link FileChooser} to pick attachments from with a pre-made {@link ExtensionFilter} for the allowed
     * media types.
     *
     * @return an asynchronous result containing the list of selected files once user is done with choosing them.
     */
    private CompletionStage<List<File>> openFileChooserForMedia() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick a media for your tweet");
        final ExtensionFilter extensionFilter = twitterMediaExtensionFilter.getExtensionFilter();
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setSelectedExtensionFilter(extensionFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return CompletableFuture.supplyAsync(() -> {
            final Stage stage = this.embeddingStage.getValue();
            final List<File> chosenFiles = fileChooser.showOpenMultipleDialog(stage);
            return chosenFiles != null ? chosenFiles : Collections.emptyList();
        }, Platform::runLater);
    }

    /**
     * Creates previews for every media selected for upload.
     *
     * @param selectedFiles the selected files
     */
    private void mediaFilesChosen(final List<File> selectedFiles) {
        pickMediaButton.setDisable(false);
        mediasToUpload.addAll(selectedFiles);
        LOG.debug("Added media files for upload with next tweet : {}", selectedFiles);
        final List<ImageView> mediaImagePreviews = selectedFiles.stream()
                                                                .map(NewTweetController::buildMediaPreviewImageView)
                                                                .filter(Objects::nonNull)
                                                                .collect(Collectors.toList());
        if (!mediaImagePreviews.isEmpty()) {
            mediaPreviewBox.getChildren().addAll(mediaImagePreviews);
        }
    }

    /**
     * Builds an {@link ImageView} preview for a given file.
     *
     * @param previewedFile The file in question
     *
     * @return The miniature {@link ImageView} previewing it
     */
    private static ImageView buildMediaPreviewImageView(final File previewedFile) {
        try {
            final ImageView imageView = new ImageView();
            imageView.setFitHeight(MEDIA_PREVIEW_IMAGE_SIZE);
            imageView.setFitWidth(MEDIA_PREVIEW_IMAGE_SIZE);
            final URL imageUrl = previewedFile.toURI().toURL();
            final Image image = new Image(
                    imageUrl.toExternalForm(),
                    MEDIA_PREVIEW_IMAGE_SIZE,
                    MEDIA_PREVIEW_IMAGE_SIZE,
                    false,
                    true
            );
            imageView.setImage(image);
            final Rectangle previewClip = Clipping.getSquareClip(
                    MEDIA_PREVIEW_IMAGE_SIZE,
                    MEDIA_PREVIEW_IMAGE_SIZE * 0.25
            );
            imageView.setClip(previewClip);
            return imageView;
        } catch (final MalformedURLException e) {
            LOG.error("Can not preview media" + previewedFile.toString(), e);
            return null;
        }
    }

}
