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
import moe.tristan.easyfxml.model.beanmanagement.Selector;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.util.Buttons;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.twitter.TwitterMediaExtensionFilter;
import moe.lyrebird.model.twitter.services.NewTweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import static moe.lyrebird.view.screens.Screens.NEW_TWEET_VIEW;

@Lazy
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewScreenController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(NewScreenController.class);

    @FXML
    private Button sendButton;

    @FXML
    private Button pickMediaButton;

    @FXML
    private TextArea tweetTextArea;

    @FXML
    private Label charactersLeft;

    private final StageManager stageManager;
    private final NewTweetService newTweetService;
    private final TwitterMediaExtensionFilter twitterMediaExtensionFilter;

    private final Set<File> mediasToUpload;

    public NewScreenController(
            final StageManager stageManager,
            final NewTweetService newTweetService,
            final TwitterMediaExtensionFilter extensionFilter
    ) {
        this.stageManager = stageManager;
        this.newTweetService = newTweetService;
        this.twitterMediaExtensionFilter = extensionFilter;
        this.mediasToUpload = new HashSet<>();
    }

    @Override
    public void initialize() {
        enableTweetLengthCheck();
        Buttons.setOnClick(sendButton, this::sendTweet);
        Buttons.setOnClick(pickMediaButton, this::openMediaAttachmentsFilePicker);
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
        newTweetService.sendNewTweet(
                tweetTextArea.getText(),
                new ArrayList<>(mediasToUpload)
        ).whenCompleteAsync((postedStatus, err) -> {
            if (err != null) {
                tweetTextArea.setDisable(false);
                sendButton.setDisable(false);
                pickMediaButton.setDisable(false);
                mediasToUpload.clear();
            } else {
                stageManager.getMultiple(NEW_TWEET_VIEW, new Selector(this.hashCode())).peek(Stages::scheduleHiding);
            }
        });
    }

    private void openMediaAttachmentsFilePicker() {
        pickMediaButton.setDisable(true);
        final CompletionStage<List<File>> pickedMedia = openFileChooserForMedia();
        pickedMedia.thenAcceptAsync(files -> {
            mediasToUpload.addAll(files);
            LOG.debug("Added media files for upload with next tweet : {}", files);
            pickMediaButton.setDisable(false);
        }, Platform::runLater);
    }

    private CompletionStage<List<File>> openFileChooserForMedia() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick a media for your tweet");
        final FileChooser.ExtensionFilter extensionFilter = twitterMediaExtensionFilter.extensionFilter;
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setSelectedExtensionFilter(extensionFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final CompletableFuture<List<File>> pickedFiles = new CompletableFuture<>();
        stageManager.getMultiple(NEW_TWEET_VIEW, new Selector(this.hashCode()))
                    .peek(newTweetStage -> Platform.runLater(
                            () -> pickedFiles.complete(
                                    fileChooser.showOpenMultipleDialog(newTweetStage)
                            )
                    ))
                    .onEmpty(() -> pickedFiles.completeExceptionally(
                            new IllegalStateException("Cannot find NewTweet window."))
                    );
        return pickedFiles;
    }

}
