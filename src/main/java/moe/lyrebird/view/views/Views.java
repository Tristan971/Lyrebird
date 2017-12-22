package moe.lyrebird.view.views;

import moe.lyrebird.view.views.fxml.*;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.api.FxmlStylesheet;

/**
 * The views as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Views implements FxmlNode {
    ROOT_VIEW("RootView.fxml", RootViewController.class),
    LOGIN_VIEW("Login.fxml", LoginViewController.class),
    TIMELINE_VIEW("Timeline.fxml", TimelineController.class),
    TWEET_VIEW("Tweet.fxml", TweetController.class),
    CONTROL_BAR("ControlBar.fxml", ControlBarController.class);

    private final String fxmlFile;
    private final Class<? extends FxmlController> controllerClass;
    private final FxmlStylesheet stylesheet;

    Views(final String fxmlFile, final Class<? extends FxmlController> controllerClass) {
        this(fxmlFile, controllerClass, FxmlStylesheet.INHERIT);
    }

    Views(final String fxmlFile, final Class<? extends FxmlController> controllerClass, final FxmlStylesheet stylesheet) {
        this.fxmlFile = fxmlFile;
        this.controllerClass = controllerClass;
        this.stylesheet = stylesheet;
    }

    @Override
    public FxmlFile getFile() {
        return () -> this.fxmlFile;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return this.controllerClass;
    }

    @Override
    public FxmlStylesheet getStylesheet() {
        return this.stylesheet;
    }
}
