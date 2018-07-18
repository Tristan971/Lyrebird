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

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.util.Buttons;
import moe.lyrebird.model.twitter.TwitterMediaExtensionFilter;
import moe.lyrebird.model.twitter.services.NewTweetService;
import moe.lyrebird.view.util.Clipping;
import moe.lyrebird.view.util.StageAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;

@Lazy
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTweetController implements FxmlController, StageAware {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetController.class);

    private static final double MEDIA_PREVIEW_IMAGE_SIZE = 32.0;

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

    private final ListProperty<File> mediasToUpload;
    private final Property<Stage> embeddingStage;

    public NewTweetController(
            final NewTweetService newTweetService,
            final TwitterMediaExtensionFilter extensionFilter
    ) {
        this.newTweetService = newTweetService;
        this.twitterMediaExtensionFilter = extensionFilter;
        this.mediasToUpload = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        this.embeddingStage = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        enableTweetLengthCheck();
        Buttons.setOnClick(sendButton, this::sendTweet);
        Buttons.setOnClick(pickMediaButton, this::openMediaAttachmentsFilePicker);

        final BooleanBinding mediasNotEmpty = mediasToUpload.emptyProperty().not();
        mediaPreviewBox.visibleProperty().bind(mediasNotEmpty);
        mediaPreviewBox.managedProperty().bind(mediasNotEmpty);
    }

    @Override
    public void setStage(final Stage embeddingStage) {
        this.embeddingStage.setValue(embeddingStage);
    }

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

    private void sendTweet() {
        Stream.of(tweetTextArea, sendButton, pickMediaButton).forEach(ctr -> ctr.setDisable(true));
        newTweetService.sendNewTweet(tweetTextArea.getText(), mediasToUpload)
                       .thenAcceptAsync(status -> {
                           LOG.info("Tweeted status : {} [{}]", status.getId(), status.getText());
                           this.embeddingStage.getValue().hide();
                       }, Platform::runLater);
    }

    private void openMediaAttachmentsFilePicker() {
        pickMediaButton.setDisable(true);
        this.openFileChooserForMedia()
            .thenAcceptAsync(
                    this::mediaFilesChosen,
                    Platform::runLater
            );
    }

    private CompletionStage<List<File>> openFileChooserForMedia() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick a media for your tweet");
        final FileChooser.ExtensionFilter extensionFilter = twitterMediaExtensionFilter.extensionFilter;
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setSelectedExtensionFilter(extensionFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        return CompletableFuture.supplyAsync(() -> {
            final Stage stage = this.embeddingStage.getValue();
            final List<File> chosenFiles = fileChooser.showOpenMultipleDialog(stage);
            return chosenFiles != null ? chosenFiles : Collections.emptyList();
        }, Platform::runLater);
    }

    private void mediaFilesChosen(final List<File> selectedFiles) {
        pickMediaButton.setDisable(false);
        mediasToUpload.addAll(selectedFiles);
        LOG.debug("Added media files for upload with next tweet : {}", selectedFiles);
        final List<ImageView> mediaImagePreviews = selectedFiles.stream()
                                                                .map(this::buildMediaPreviewImageView)
                                                                .filter(Objects::nonNull)
                                                                .collect(Collectors.toList());
        if (!mediaImagePreviews.isEmpty()) {
            mediaPreviewBox.getChildren().addAll(mediaImagePreviews);
        }
    }

    private ImageView buildMediaPreviewImageView(final File previewedFile) {
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
