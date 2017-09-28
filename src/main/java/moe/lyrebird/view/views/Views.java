package moe.lyrebird.view.views;

import moe.tristan.easyfxml.FxmlFile;

/**
 * The views as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Views implements FxmlFile {
    ROOT_VIEW("RootView.fxml"),
    LOGIN_VIEW("Login.fxml"),
    TIMELINE_VIEW("Timeline.fxml"),
    TWEET_VIEW("Tweet.fxml");
    
    private final String fileName;
    
    Views(final String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public String getPath() {
        return this.fileName;
    }
}
