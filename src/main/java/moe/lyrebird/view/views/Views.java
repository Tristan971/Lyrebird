package moe.lyrebird.view.views;

/**
 * The views as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Views {
    VIEWS_ROOT_FOLDER("moe/lyrebird/view/views/fxml/"),
    
    ROOT_VIEW("RootView.fxml"),
    LOGIN_VIEW("Login.fxml"),
    TIMELINE_VIEW("Timeline.fxml"),
    TWEET_VIEW("Tweet.fxml");
    
    private final String fileName;
    
    Views(final String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public String toString() {
        return this.fileName;
    }
}
