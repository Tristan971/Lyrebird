package moe.lyrebird.view.views;

import moe.lyrebird.view.views.fxml.controlbar.ControlBarController;
import moe.lyrebird.view.views.fxml.login.LoginViewController;
import moe.lyrebird.view.views.fxml.newtweet.NewTweetController;
import moe.lyrebird.view.views.fxml.root.RootViewController;
import moe.lyrebird.view.views.fxml.timeline.TimelineController;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;

/**
 * The views as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Views implements FxmlNode {
    ROOT_VIEW("root/RootView.fxml", RootViewController.class),
    LOGIN_VIEW("login/Login.fxml", LoginViewController.class),
    TIMELINE_VIEW("timeline/Timeline.fxml", TimelineController.class),
    TWEET_VIEW("newtweet/NewTweet.fxml", NewTweetController.class),
    CONTROL_BAR("controlbar/ControlBar.fxml", ControlBarController.class);

    private final String fxmlFile;
    private final Class<? extends FxmlController> controllerClass;

    Views(final String fxmlFile, final Class<? extends FxmlController> controllerClass) {
        this.fxmlFile = fxmlFile;
        this.controllerClass = controllerClass;
    }

    @Override
    public FxmlFile getFile() {
        return () -> this.fxmlFile;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return this.controllerClass;
    }

}
