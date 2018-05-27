package moe.lyrebird.view.screens;

import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.lyrebird.view.screens.login.LoginViewController;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.screens.root.RootViewController;

/**
 * The screens as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Screens implements FxmlNode {
    ROOT_VIEW("root/RootView.fxml", RootViewController.class),
    LOGIN_VIEW("login/Login.fxml", LoginViewController.class),
    TWEET_VIEW("newtweet/NewTweet.fxml", NewTweetController.class);

    private static final String SCREENS_BASE_PATH = "moe/lyrebird/view/screens/";

    private final String fxmlFile;
    private final Class<? extends FxmlController> controllerClass;

    Screens(final String fxmlFile, final Class<? extends FxmlController> controllerClass) {
        this.fxmlFile = fxmlFile;
        this.controllerClass = controllerClass;
    }

    @Override
    public FxmlFile getFile() {
        return () -> SCREENS_BASE_PATH + fxmlFile;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return controllerClass;
    }

}
