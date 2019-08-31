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

package moe.lyrebird.view.components.controlbar;

import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.update.UpdateService;
import moe.lyrebird.view.components.currentaccount.CurrentAccountComponent;
import moe.lyrebird.view.components.directmessages.DirectMessagesComponent;
import moe.lyrebird.view.components.mentions.MentionsComponent;
import moe.lyrebird.view.components.timeline.TimelineComponent;
import moe.lyrebird.view.screens.credits.CreditScreenComponent;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.screens.newtweet.NewTweetScreenComponent;
import moe.lyrebird.view.screens.root.RootScreenController;
import moe.lyrebird.view.screens.update.UpdateScreenComponent;
import moe.lyrebird.view.viewmodel.javafx.Clipping;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlComponent;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;

import io.vavr.control.Option;

/**
 * The ControlBar is the left-side view selector for Lyrebird's main UI window.
 */
@Component
public class ControlBarController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(ControlBarController.class);

    @FXML
    private BorderPane container;

    @FXML
    private HBox tweet;

    @FXML
    private HBox timeline;

    @FXML
    private HBox mentions;

    @FXML
    private HBox directMessages;

    @FXML
    private HBox credits;

    @FXML
    private HBox update;

    private final EasyFxml easyFxml;
    private final RootScreenController rootScreenController;
    private final SessionManager sessionManager;
    private final UpdateService updateService;
    private final StageManager stageManager;

    private final TimelineComponent timelineComponent;
    private final MentionsComponent mentionsComponent;
    private final DirectMessagesComponent directMessagesComponent;
    private final CurrentAccountComponent currentAccountComponent;

    private final NewTweetScreenComponent newTweetScreenComponent;
    private final CreditScreenComponent creditScreenComponent;
    private final UpdateScreenComponent updateScreenComponent;

    private final Property<HBox> currentViewButton;

    public ControlBarController(
            EasyFxml easyFxml,
            RootScreenController rootScreenController,
            SessionManager sessionManager,
            UpdateService updateService,
            StageManager stageManager,
            TimelineComponent timelineComponent,
            MentionsComponent mentionsComponent,
            DirectMessagesComponent directMessagesComponent,
            CurrentAccountComponent currentAccountComponent,
            NewTweetScreenComponent newTweetScreenComponent,
            CreditScreenComponent creditScreenComponent,
            UpdateScreenComponent updateScreenComponent
    ) {
        this.easyFxml = easyFxml;
        this.rootScreenController = rootScreenController;
        this.sessionManager = sessionManager;
        this.updateService = updateService;
        this.stageManager = stageManager;
        this.timelineComponent = timelineComponent;
        this.mentionsComponent = mentionsComponent;
        this.directMessagesComponent = directMessagesComponent;
        this.currentAccountComponent = currentAccountComponent;
        this.newTweetScreenComponent = newTweetScreenComponent;
        this.creditScreenComponent = creditScreenComponent;
        this.updateScreenComponent = updateScreenComponent;
        this.currentViewButton = new SimpleObjectProperty<>(null);
    }

    @Override
    public void initialize() {
        currentViewButton.addListener((o, prev, current) -> {
            if (prev != null) {
                prev.setDisable(false);
                prev.setOpacity(1.0);
            }
            current.setDisable(true);
            current.setOpacity(0.5);
        });

        setUpTweetButton();

        bindActionImageToLoadingView(timeline, timelineComponent);
        bindActionImageToLoadingView(mentions, mentionsComponent);
        bindActionImageToLoadingView(directMessages, directMessagesComponent);

        credits.setOnMouseClicked(e -> openCreditsView());

        sessionManager.isLoggedInProperty().addListener((o, prev, cur) -> handleLogStatusChange(prev, cur));
        handleLogStatusChange(false, sessionManager.isLoggedInProperty().getValue());
        loadCurrentAccountPanel();

        update.managedProperty().bind(updateService.isUpdateAvailableProperty());
        update.visibleProperty().bind(updateService.isUpdateAvailableProperty());
        update.setOnMouseClicked(e -> openUpdatesScreen());
    }

    /**
     * Makes sure the tweet button appears as a nice circle through CSS and clipping work.
     */
    private void setUpTweetButton() {
        tweet.setOnMouseClicked(e -> this.openTweetWindow());
        tweet.setClip(Clipping.getCircleClip(28.0));
    }

    /**
     * Loads the current user's account view on the top of the bar.
     */
    private void loadCurrentAccountPanel() {
        easyFxml.load(currentAccountComponent)
                .getNode()
                .onSuccess(container::setTop)
                .onFailure(err -> displayExceptionPane(
                        "Could not load current user!",
                        "There was an error mapping the current session to a twitter account.",
                        err
                ));
    }

    /**
     * Called on click on the {@link #tweet} box. Opens a new tweet window.
     *
     * @see NewTweetScreenComponent
     */
    private void openTweetWindow() {
        LOG.info("Opening new tweet stage...");
        final FxmlLoadResult<Pane, NewTweetController> newTweetViewLoadResult = this.easyFxml.load(
                newTweetScreenComponent,
                Pane.class,
                NewTweetController.class
        );
        final Pane newTweetPane = newTweetViewLoadResult.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        final NewTweetController newTweetController = newTweetViewLoadResult.getController().get();

        Stages.stageOf("Tweet", newTweetPane)
              .thenComposeAsync(Stages::scheduleDisplaying)
              .thenAcceptAsync(newTweetController::setStage);
    }

    /**
     * This method managed switching from an unlogged to a logged state. It is tied to {@link SessionManager#isLoggedInProperty()}'s value.
     *
     * @param previous Whether the user was previously logged-in
     * @param current  Whether the user is not logged-in
     */
    private void handleLogStatusChange(final boolean previous, final boolean current) {
        List.of(
                timeline,
                tweet,
                mentions,
                directMessages,
                tweet
        ).forEach(btn -> btn.setVisible(current));

        // was not connected and now is, mostly to distinguish with the future feature of
        // multiple accounts management
        if (!previous && current) {
            timeline.onMouseClickedProperty().get().handle(null);
        }
    }

    private void bindActionImageToLoadingView(final HBox imageBox, final FxmlComponent fxComponent) {
        imageBox.setOnMouseClicked(e -> {
            currentViewButton.setValue(imageBox);
            rootScreenController.setContent(fxComponent);
        });
    }

    private void openCreditsView() {
        final Option<Stage> existingCreditsStage = stageManager.getSingle(creditScreenComponent);
        if (existingCreditsStage.isDefined()) {
            final Stage creditsStage = existingCreditsStage.get();
            creditsStage.show();
            creditsStage.toFront();
        } else {
            loadCreditsStage().thenAcceptAsync(stage -> {
                stageManager.registerSingle(creditScreenComponent, stage);
                Stages.scheduleDisplaying(stage);
            });
        }
    }

    private CompletionStage<Stage> loadCreditsStage() {
        return easyFxml.load(creditScreenComponent)
                       .orExceptionPane()
                       .map(pane -> Stages.stageOf("Credits", pane))
                       .getOrElseThrow((Function<? super Throwable, ? extends RuntimeException>) RuntimeException::new);
    }

    /**
     * The {@link #update} box only show up when an update is detected as available. Then if it is the case, this method is called on click to open the update
     * information screen.
     *
     * @see UpdateScreenComponent
     */
    private void openUpdatesScreen() {
        final FxmlLoadResult<Pane, FxmlController> updateScreenLoadResult = easyFxml.load(updateScreenComponent);
        final Pane updatePane = updateScreenLoadResult.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        Stages.stageOf("Updates", updatePane).thenAcceptAsync(Stages::scheduleDisplaying);
    }

}
