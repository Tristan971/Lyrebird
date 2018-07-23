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
import moe.lyrebird.model.twitter.services.interraction.StatusInterraction;
import moe.lyrebird.model.twitter.services.interraction.TwitterBinaryInterraction;
import moe.lyrebird.model.twitter.services.interraction.TwitterInterractionService;
import moe.lyrebird.view.assets.ImageResources;
import moe.lyrebird.view.screens.Screen;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.util.Clipping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.concurrent.CompletableFuture;

import static moe.lyrebird.model.twitter.services.interraction.StatusInterraction.LIKE;
import static moe.lyrebird.model.twitter.services.interraction.StatusInterraction.RETWEET;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetInterractionPaneController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(TweetInterractionPaneController.class);

    private static final double INTERRACTION_BUTTON_CLIP_RADIUS = 14.0;

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

    private final TwitterInterractionService interractionService;
    private final EasyFxml easyFxml;
    private final Property<Status> targetStatus;

    @Autowired
    public TweetInterractionPaneController(
            TwitterInterractionService interractionService,
            EasyFxml easyFxml
    ) {
        this.interractionService = interractionService;
        this.easyFxml = easyFxml;
        this.targetStatus = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        setUpInterractionActions();
        targetStatus.addListener((o, prev, cur) -> {
            updateRetweetVisual(cur.isRetweet() ? cur.getRetweetedStatus().isRetweeted() : cur.isRetweeted());
            updateLikeVisual(cur.isFavorited());
        });
    }

    public Property<Status> targetStatusProperty() {
        return targetStatus;
    }

    private void setUpInterractionActions() {
        replyButton.setOnMouseClicked(e -> this.onReply());
        replyButton.setClip(Clipping.getCircleClip(INTERRACTION_BUTTON_CLIP_RADIUS));

        likeButton.setOnMouseClicked(e -> this.onLike());
        likeButton.setClip(Clipping.getCircleClip(INTERRACTION_BUTTON_CLIP_RADIUS));

        retweetButton.setOnMouseClicked(e -> this.onRetweet());
        retweetButton.setClip(Clipping.getCircleClip(INTERRACTION_BUTTON_CLIP_RADIUS));
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
     *
     * @see TwitterInterractionService
     * @see TwitterBinaryInterraction
     * @see StatusInterraction#RETWEET
     */
    private void onRetweet() {
        LOG.debug("Retweet interraction on status {}", targetStatus.getValue().getId());
        retweetButton.setDisable(true);
        CompletableFuture.supplyAsync(
                () -> interractionService.interract(targetStatus.getValue(), RETWEET)
        ).thenAcceptAsync(res -> {
            updateRetweetVisual(!interractionService.notYetRetweeted(targetStatus.getValue()));
            retweetButton.setDisable(false);
        }, Platform::runLater);
    }

    private void updateRetweetVisual(final boolean retweeted) {
        retweetButtonGraphic.setImage(
                retweeted ?
                ImageResources.TWEETPANE_RETWEET_ON.getImage() :
                ImageResources.TWEETPANE_RETWEET_OFF.getImage()
        );
    }

    /**
     * Called on click of the like button.
     *
     * @see TwitterInterractionService
     * @see TwitterBinaryInterraction
     * @see StatusInterraction#LIKE
     */
    private void onLike() {
        LOG.debug("Like interraction on status {}", targetStatus.getValue().getId());
        likeButton.setDisable(true);
        CompletableFuture.supplyAsync(
                () -> interractionService.interract(targetStatus.getValue(), LIKE)
        ).thenAcceptAsync(res -> {
            updateLikeVisual(res.isFavorited());
            likeButton.setDisable(false);
        }, Platform::runLater);
    }

    private void updateLikeVisual(final boolean liked) {
        likeButtonGraphic.setImage(
                liked ?
                ImageResources.TWEETPANE_LIKE_ON.getImage() :
                ImageResources.TWEETPANE_LIKE_OFF.getImage()
        );
    }

}
