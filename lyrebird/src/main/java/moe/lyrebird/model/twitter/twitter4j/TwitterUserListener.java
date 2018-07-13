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

package moe.lyrebird.model.twitter.twitter4j;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import moe.lyrebird.model.twitter.observables.Mentions;
import moe.lyrebird.model.twitter.observables.Timeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

import java.net.SocketException;

@Component
public class TwitterUserListener implements UserStreamListener {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterUserListener.class);

    private final Timeline timeline;
    private final Mentions mentions;
    private final DirectMessages directMessages;

    public TwitterUserListener(
            final Timeline timeline,
            final Mentions mentions,
            final DirectMessages directMessages
    ) {
        LOG.debug("Initializing twitter data listener.");
        LOG.debug("\t-> Timeline... OK");
        this.timeline = timeline;
        LOG.debug("\t-> Mentions... OK");
        this.mentions = mentions;
        LOG.debug("\t-> DMs... OK");
        this.directMessages = directMessages;
    }

    @Override
    public void onStatus(final Status status) {
        LOG.debug("New tweet streamed : [@{} : {}]", status.getUser().getScreenName(), status.getText());
        timeline.addTweet(status);
        if (mentions.isMentionToCurrentUser(status)) {
            mentions.addTweet(status);
        }
    }

    @Override
    public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice) {
        LOG.debug("New tweet deletion : {}", statusDeletionNotice.getStatusId());
        timeline.removeTweet(statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(final int numberOfLimitedStatuses) {
        LOG.debug("Current streamed track is too fast for following. Dropping tweets.");
    }

    @Override
    public void onScrubGeo(final long userId, final long upToStatusId) {
        LOG.debug("Location info removad request for tweets by {} until tweet {}.", userId, upToStatusId);
    }

    @Override
    public void onStallWarning(final StallWarning warning) {
        LOG.debug("Internet connection was too slow and could not keep-alive the stream.");
        ExceptionHandler.displayExceptionPane(
                "Stall warning",
                "Our persistent streaming connection to Twitter has died due to network issues.",
                new SocketException(warning.toString())
        );
    }

    @Override
    public void onException(final Exception ex) {
        LOG.error("Twitter streaming broke.", ex);
        ExceptionHandler.displayExceptionPane(
                "Exception on twitter streaming service.",
                "The twitter streaming service could not be set-up/continued.",
                ex
        );
    }

    @Override
    public void onDeletionNotice(final long directMessageId, final long userId) {
        LOG.debug("DM {} from {} requested to be deleted.", directMessageId, userId);
        directMessages.removeDirectMessage(userId, directMessageId);
    }

    @Override
    public void onFriendList(final long[] friendIds) {
        // ignored
    }

    @Override
    public void onFavorite(final User source, final User target, final Status favoritedStatus) {
        // ignored
    }

    @Override
    public void onUnfavorite(final User source, final User target, final Status unfavoritedStatus) {
        // ignored
    }

    @Override
    public void onFollow(final User source, final User followedUser) {
        // ignored
    }

    @Override
    public void onUnfollow(final User source, final User unfollowedUser) {
        // ignored
    }

    @Override
    public void onDirectMessage(final DirectMessage directMessage) {
        LOG.debug("Received DM {}", directMessage);
        directMessages.addDirectMessage(directMessage);
    }

    @Override
    public void onUserListMemberAddition(final User addedMember, final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListMemberDeletion(final User deletedMember, final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListSubscription(final User subscriber, final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListUnsubscription(final User subscriber, final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListCreation(final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListUpdate(final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserListDeletion(final User listOwner, final UserList list) {
        // ignored
    }

    @Override
    public void onUserProfileUpdate(final User updatedUser) {
        // ignored
    }

    @Override
    public void onUserSuspension(final long suspendedUser) {
        // ignored
    }

    @Override
    public void onUserDeletion(final long deletedUser) {
        // ignored
    }

    @Override
    public void onBlock(final User source, final User blockedUser) {
        // ignored
    }

    @Override
    public void onUnblock(final User source, final User unblockedUser) {
        // ignored
    }

    @Override
    public void onRetweetedRetweet(final User source, final User target, final Status retweetedStatus) {
        // ignored
    }

    @Override
    public void onFavoritedRetweet(final User source, final User target, final Status favoritedRetweeet) {
        // ignored
    }

    @Override
    public void onQuotedTweet(final User source, final User target, final Status quotingTweet) {
        // ignored
    }

}
