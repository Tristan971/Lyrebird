package moe.lyrebird.view.components.tweet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.twitter.services.interraction.StatusInteraction;
import moe.lyrebird.model.twitter.services.interraction.TwitterBinaryInteraction;
import moe.lyrebird.model.twitter.services.interraction.TwitterInteractionService;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.Screen;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.viewmodel.javafx.Clipping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Status;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.model.twitter.services.interraction.StatusInteraction.LIKE;
import static moe.lyrebird.model.twitter.services.interraction.StatusInteraction.RETWEET;

/**
 * This class manages the display of an interaction toolbar under every non-embedded tweet.
 * <p>
 * Its role is to enable interaction and reflect it visually.
 *
 * @see TweetPaneController
 * @see TwitterInteractionService
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetInteractionPaneController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(TweetInteractionPaneController.class);

    private static final double INTERACTION_BUTTON_CLIP_RADIUS = 14.0;

    @FXML
    private HBox replyButton;

    @FXML
    private HBox likeButton;

    @FXML
    private ImageView likeButtonGraphic;

    @FXML
    private HBox retweetButton;

    @FXML
    private ImageView retweetButtonGraphic;

    private final TwitterInteractionService interactionService;
    private final EasyFxml easyFxml;
    private final Property<Status> targetStatus;

    @Autowired
    public TweetInteractionPaneController(
            TwitterInteractionService interactionService,
            EasyFxml easyFxml
    ) {
        this.interactionService = interactionService;
        this.easyFxml = easyFxml;
        this.targetStatus = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        setUpInteractionActions();
        targetStatus.addListener((o, prev, cur) -> {
            updateRetweetVisual(cur.isRetweet() ? cur.getRetweetedStatus().isRetweeted() : cur.isRetweeted());
            updateLikeVisual(cur.isFavorited());
        });
    }

    Property<Status> targetStatusProperty() {
        return targetStatus;
    }

    /**
     * Binds click on a button to its expected action
     */
    private void setUpInteractionActions() {
        replyButton.setOnMouseClicked(e -> this.onReply());
        replyButton.setClip(Clipping.getCircleClip(INTERACTION_BUTTON_CLIP_RADIUS));

        likeButton.setOnMouseClicked(e -> this.onLike());
        likeButton.setClip(Clipping.getCircleClip(INTERACTION_BUTTON_CLIP_RADIUS));

        retweetButton.setOnMouseClicked(e -> this.onRetweet());
        retweetButton.setClip(Clipping.getCircleClip(INTERACTION_BUTTON_CLIP_RADIUS));
    }

    /**
     * Opens a {@link Screen#NEW_TWEET_VIEW} with the current status embedded for reply features.
     */
    private void onReply() {
        final FxmlLoadResult<Pane, NewTweetController> replyStageLoad = easyFxml.loadNode(
                Screen.NEW_TWEET_VIEW,
                Pane.class,
                NewTweetController.class
        ).afterControllerLoaded(ntc -> ntc.setInReplyToTweet(targetStatus.getValue()));

        final NewTweetController newTweetController = replyStageLoad.getController().get();
        final Pane newTweetPane = replyStageLoad.getNode().getOrElseGet(ExceptionHandler::fromThrowable);

        Stages.stageOf("Reply to tweet", newTweetPane)
              .thenAcceptAsync(stage -> {
                  newTweetController.setStage(stage);
                  Stages.scheduleDisplaying(stage);
              });
    }

    /**
     * Called on click of the retweet button.
     * <p>
     * Will call {@link #updateRetweetVisual(boolean)} on task finish to set the appropriate visual value.
     *
     * @see TwitterInteractionService
     * @see TwitterBinaryInteraction
     * @see StatusInteraction#RETWEET
     */
    private void onRetweet() {
        LOG.debug("Retweet interaction on status {}", targetStatus.getValue().getId());
        retweetButton.setDisable(true);
        CompletableFuture.supplyAsync(
                () -> interactionService.interact(targetStatus.getValue(), RETWEET)
        ).thenAcceptAsync(res -> {
            final Status originalStatus = targetStatus.getValue().isRetweet() ?
                                          targetStatus.getValue().getRetweetedStatus() :
                                          targetStatus.getValue();
            updateRetweetVisual(!interactionService.notYetRetweeted(originalStatus));
            retweetButton.setDisable(false);
        }, Platform::runLater);
    }

    /**
     * Updates the visual clue for whether the current tweet was already retweeted by the current user.
     *
     * @param retweeted whether the current tweet was determined to having been retweeted by the current user
     */
    private void updateRetweetVisual(final boolean retweeted) {
        retweetButtonGraphic.setImage(
                retweeted ?
                ImageResources.TWEETPANE_RETWEET_ON.getImage() :
                ImageResources.TWEETPANE_RETWEET_OFF.getImage()
        );
    }

    /**
     * Called on click of the like button.
     * <p>
     * Will call {@link #updateLikeVisual(boolean)} on task finish to set the appropriate visual value.
     *
     * @see TwitterInteractionService
     * @see TwitterBinaryInteraction
     * @see StatusInteraction#LIKE
     */
    private void onLike() {
        LOG.debug("Like interaction on status {}", targetStatus.getValue().getId());
        likeButton.setDisable(true);
        CompletableFuture.supplyAsync(
                () -> interactionService.interact(targetStatus.getValue(), LIKE)
        ).thenAcceptAsync(res -> {
            updateLikeVisual(res.isFavorited());
            likeButton.setDisable(false);
        }, Platform::runLater);
    }

    /**
     * Updates the visual clue for whether the current tweet was already liked by the current user.
     *
     * @param liked whether the current tweet was determined to having been liked by the current user
     */
    private void updateLikeVisual(final boolean liked) {
        likeButtonGraphic.setImage(
                liked ?
                ImageResources.TWEETPANE_LIKE_ON.getImage() :
                ImageResources.TWEETPANE_LIKE_OFF.getImage()
        );
    }

}
