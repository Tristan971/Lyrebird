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

package moe.lyrebird.model.notifications.format;

import moe.lyrebird.model.notifications.Notification;
import twitter4a.Status;
import twitter4a.User;

/**
 * Helper methods for formatting notifications related to twitter events.
 */
public final class TwitterNotifications {

    private TwitterNotifications() {
        throw new AssertionError("Not instantiable.");
    }

    public static Notification fromMention(final Status mention) {
        final String title = mention.getUser().getName() + " mentioned you";
        return new Notification(title, mention.getText());
    }

    public static Notification fromFavorite(final User favoriter, final Status tweet) {
        final String title = favoriter.getName() + " liked one of your tweets:";
        return new Notification(title, tweet.getText());
    }

    public static Notification fromRetweet(final Status retweetingStatus) {
        final String title = retweetingStatus.getUser().getName() + " retweeted you";
        return new Notification(title, retweetingStatus.getRetweetedStatus().getText());
    }

    public static Notification fromQuotedTweet(final Status quotingStatus) {
        final String title = quotingStatus.getUser().getName() + " quoted you";
        return new Notification(
                title,
                quotingStatus.getText() + "\n\n Quoted:\n" + quotingStatus.getQuotedStatus().getText()
        );
    }

    public static Notification fromFollow(final User follower) {
        final String title = follower.getName() + " started following you!";
        return new Notification(title, "");
    }

    public static Notification fromUnfollow(final User unfollower) {
        final String title = unfollower.getName() + " unfollowed you :(";
        return new Notification(title, "unlucky... :(");
    }

}
