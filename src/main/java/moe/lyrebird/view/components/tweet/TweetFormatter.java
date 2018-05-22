package moe.lyrebird.view.components.tweet;

import twitter4j.Status;
import twitter4j.User;

import javafx.scene.image.Image;

public final class TweetFormatter {

    private TweetFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String username(final User user) {
        return user.getName() + " | @" + user.getScreenName();
    }

    public static Image userProfileImage(final User user) {
        return new Image(user.getProfileImageURL());
    }

    public static String tweetContent(final Status status) {
        return status.getText();
    }

}
