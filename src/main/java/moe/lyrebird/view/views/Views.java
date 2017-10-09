package moe.lyrebird.view.views;

import io.vavr.control.Option;
import moe.lyrebird.view.views.fxml.LoginViewController;
import moe.lyrebird.view.views.fxml.RootViewController;
import moe.lyrebird.view.views.fxml.TimelineController;
import moe.lyrebird.view.views.fxml.TweetController;
import moe.tristan.easyfxml.model.FxmlController;
import moe.tristan.easyfxml.model.FxmlFile;
import moe.tristan.easyfxml.model.FxmlNode;
import moe.tristan.easyfxml.model.FxmlStylesheet;

/**
 * The views as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Views implements FxmlNode {
    ROOT_VIEW("RootView.fxml", Option.of(RootViewController.class)),
    LOGIN_VIEW("Login.fxml", Option.of(LoginViewController.class)),
    TIMELINE_VIEW("Timeline.fxml", Option.of(TimelineController.class)),
    TWEET_VIEW("Tweet.fxml", Option.of(TweetController.class));

    private final String fxmlFile;
    private final Option<Class<? extends FxmlController>> controllerClass;
    private final Option<FxmlStylesheet> stylesheet;

    Views(String fxmlFile, Option<Class<? extends FxmlController>> controllerClass) {
        this(fxmlFile, controllerClass, Option.none());
    }

    Views(String fxmlFile, Option<Class<? extends FxmlController>> controllerClass, Option<FxmlStylesheet> stylesheet) {
        this.fxmlFile = fxmlFile;
        this.controllerClass = controllerClass;
        this.stylesheet = stylesheet;
    }

    @Override
    public FxmlFile getFxmlFile() {
        return () -> this.fxmlFile;
    }

    @Override
    public Option<Class<? extends FxmlController>> getControllerClass() {
        return this.controllerClass;
    }

    @Override
    public Option<FxmlStylesheet> getStylesheet() {
        return this.stylesheet;
    }
}
