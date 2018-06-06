package moe.lyrebird.model.twitter.twitter4j;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.lyrebird.model.twitter.observables.DirectMessages;
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
    private final DirectMessages directMessages;

    public TwitterUserListener(
            final Timeline timeline,
            final DirectMessages directMessages
    ) {
        LOG.debug("Initializing twitter streaming listening.");
        LOG.debug("Starting timeline management...");
        this.timeline = timeline;
        LOG.debug("Starting dirrect messages management...");
        this.directMessages = directMessages;
        LOG.debug("Initialized twitter streaming listener!");
    }

    @Override
    public void onStatus(Status status) {
        LOG.debug("New tweet streamed : {}", status);
        timeline.addTweet(status);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        LOG.debug("New tweet deletion : {}", statusDeletionNotice.getStatusId());
        timeline.removeTweet(statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        LOG.debug("Current streamed track is too fast for following. Dropping tweets.");
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        LOG.debug("Location info removad request for tweets by {} until tweet {}.", userId, upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        LOG.debug("Internet connection was too slow and could not keep-alive the stream.");
        ExceptionHandler.displayExceptionPane(
                "Stall warning",
                "Our persistent streaming connection to Twitter has died due to network issues.",
                new SocketException(warning.toString())
        );
    }

    @Override
    public void onException(Exception ex) {
        LOG.error("Twitter streaming broke.", ex);
        ExceptionHandler.displayExceptionPane(
                "Exception on twitter streaming service.",
                "The twitter streaming service could not be set-up/continued.",
                ex
        );
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {
        LOG.debug("DM {} from {} requested to be deleted.", directMessageId, userId);
        directMessages.removeDirectMessage(userId, directMessageId);
    }

    @Override
    public void onFriendList(long[] friendIds) {

    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {

    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

    }

    @Override
    public void onFollow(User source, User followedUser) {

    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        LOG.debug("Received DM {}", directMessage);
        directMessages.addDirectMessage(directMessage);
    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {

    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {

    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {

    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {

    }

    @Override
    public void onUserSuspension(long suspendedUser) {

    }

    @Override
    public void onUserDeletion(long deletedUser) {

    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }

    @Override
    public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {

    }

    @Override
    public void onFavoritedRetweet(User source, User target, Status favoritedRetweeet) {

    }

    @Override
    public void onQuotedTweet(User source, User target, Status quotingTweet) {

    }
}
