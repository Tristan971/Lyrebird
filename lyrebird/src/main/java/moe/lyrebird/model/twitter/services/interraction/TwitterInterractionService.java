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

package moe.lyrebird.model.twitter.services.interraction;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Relationship;
import twitter4a.Status;
import twitter4a.Twitter;
import twitter4a.User;

import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

/**
 * This service is responsible for interractions with Twitter elements.
 * <p>
 * Most notably it manages the linking/retweeting of tweets and the following/unfollowing of users.
 */
@Component
public class TwitterInterractionService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterInterractionService.class);

    private final SessionManager sessionManager;

    public TwitterInterractionService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Executes a given {@link TwitterBinaryInterraction} on an element.
     *
     * @param target                    The element this interraction will target
     * @param twitterBinaryInterraction The interraction to execute
     * @param <T>                       The type of element this will target
     *
     * @return The resulting operation's result which is of the type of the element this is targetting
     */
    public <T> T interract(final T target, final TwitterBinaryInterraction<T> twitterBinaryInterraction) {
        if (twitterBinaryInterraction.shouldDo().apply(this, target)) {
            return twitterBinaryInterraction.onTrue().apply(this, target);
        } else {
            return twitterBinaryInterraction.onFalse().apply(this, target);
        }
    }

    /**
     * Determines whether a given tweet is a retweet made by the current user. Twitter's API really is unhelpful on this
     * side so we mostly take an educated guess here, although it should be enough in most cases.
     *
     * @param status the tweet to test against
     *
     * @return true if and only if the given status is a retweet made by the current user
     */
    public boolean isRetweetByCurrentUser(final Status status) {
        if (status.isRetweet()) {
            final Status retweetedStatus = status.getRetweetedStatus();
            return retweetedStatus.isRetweeted() ||
                   retweetedStatus.isRetweetedByMe() ||
                   sessionManager.isCurrentUser(status.getUser());
        } else {
            return false;
        }
    }

    /**
     * Likes a given tweet
     *
     * @param tweet the tweet to like
     *
     * @return the liked version of the tweet
     */
    Status like(final Status tweet) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.createFavorite(tweet.getId()))
                             .onSuccess(resultingStatus -> LOG.debug(
                                     "User {} liked tweet {}",
                                     getCurrentScreenName(),
                                     resultingStatus.getId()
                             ))
                             .onFailure(err -> displayExceptionPane("Could not like tweet!", err.getMessage(), err))
                             .get();
    }

    /**
     * Unlikes a tweet
     *
     * @param tweet the tweet to unlike
     *
     * @return the unliked version of the tweet
     */
    Status unlike(final Status tweet) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.destroyFavorite(tweet.getId()))
                             .onSuccess(resultingStatus -> LOG.debug(
                                     "User {} unliked tweet {}",
                                     getCurrentScreenName(),
                                     resultingStatus.getId()
                             ))
                             .onFailure(err -> displayExceptionPane("Could not unlike tweet!", err.getMessage(), err))
                             .get();
    }

    /**
     * Returns whether or not the given tweet has not yet been liked and that thus the interraction with it should be to
     * like it.
     *
     * @param tweet the tweet to check
     *
     * @return true if the given tweet is not liked yet but the current user
     */
    public boolean notYetLiked(final Status tweet) {
        return !sessionManager.doWithCurrentTwitter(twitter -> twitter.showStatus(tweet.getId()).isFavorited())
                              .get();
    }

    /**
     * Retweets a given tweet
     *
     * @param tweet the tweet to retweet
     *
     * @return the tweet's retweet-created tweet (a retweet is a tweet from the retweeting user)
     */
    Status retweet(final Status tweet) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.retweetStatus(tweet.getId()))
                             .onSuccess(resultingStatus -> LOG.debug(
                                     "User {} retweeted tweet {}",
                                     getCurrentScreenName(),
                                     resultingStatus.getId()
                             ))
                             .onFailure(err -> displayExceptionPane("Could not retweet tweet!", err.getMessage(), err))
                             .get();
    }

    /**
     * Unretweets (deletes the retweet-created tweet for the current user. See {@link #retweet(Status)} for explanation
     * on that).
     *
     * @param tweet the tweet to unretweet
     *
     * @return The retweet that was deleted
     */
    Status unretweet(final Status tweet) {
        final Status original = tweet.isRetweet() ? tweet.getRetweetedStatus() : tweet;
        return sessionManager.doWithCurrentTwitter(
                twitter -> twitter.unRetweetStatus(original.getId())
        ).onSuccess(resultingStatus -> LOG.debug(
                "User {} unretweeted tweet {}",
                getCurrentScreenName(),
                resultingStatus.getId()
        )).onFailure(err -> displayExceptionPane(
                "Could not unretweet tweet!",
                err.getMessage(),
                err
        )).get();
    }

    /**
     * Checks whether a given tweet has been retweeted by the current user.
     * <p>
     * PSA : I don't care that you can retweet your own tweets. This is stupid and you should never do it. Will never
     * allow a PR "fixing" that pass.
     *
     * @param tweet the tweet to check
     *
     * @return Whether the given tweet had not yet been retweeted by the current user.
     */
    public boolean notYetRetweeted(final Status tweet) {
        return !sessionManager.doWithCurrentTwitter(twitter -> {
            final Status updatedTweet = twitter.showStatus(tweet.getId());
            final Status originalStatus = updatedTweet.isRetweet() ? updatedTweet.getRetweetedStatus() : updatedTweet;
            return originalStatus.isRetweeted();
        }).get();
    }

    /**
     * Follows a given user.
     *
     * @param user the user to follow.
     *
     * @return the followed user
     */
    User follow(final User user) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.createFriendship(user.getId()))
                             .onSuccess(userFollowed -> LOG.debug(
                                     "User {} followed user {}",
                                     getCurrentScreenName(),
                                     userFollowed.getScreenName()
                             ))
                             .onFailure(err -> displayExceptionPane("Could not follow user!", err.getMessage(), err))
                             .get();
    }

    /**
     * Unfollows a user
     *
     * @param user the user to unfollow
     *
     * @return the unfollowed user
     */
    User unfollow(final User user) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.destroyFriendship(user.getId()))
                             .onSuccess(userUnfollowed -> LOG.debug(
                                     "User {} unfollowed user {}",
                                     getCurrentScreenName(),
                                     userUnfollowed.getScreenName()
                             ))
                             .onFailure(err -> displayExceptionPane("Could not unfollow user!", err.getMessage(), err))
                             .get();
    }

    /**
     * Checks if the given user has not yet been followed
     *
     * @param user The user for which to check the follow status
     *
     * @return true if and only if the given user has not yet been followed by the current user
     */
    public boolean notYetFollowed(final User user) {
        return !sessionManager.doWithCurrentTwitter(twitter -> twitter.showFriendship(
                getCurrentScreenName(),
                user.getScreenName()
        )).map(Relationship::isSourceFollowingTarget).get();
    }

    /**
     * @return the updated screen name of the current user
     */
    private String getCurrentScreenName() {
        return sessionManager.getCurrentTwitter()
                             .mapTry(Twitter::getScreenName)
                             .getOrElseThrow(err -> new IllegalStateException("Current user unavailable!", err));
    }

}
