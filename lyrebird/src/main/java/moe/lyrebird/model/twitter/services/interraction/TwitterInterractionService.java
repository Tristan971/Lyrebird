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
import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

import static moe.tristan.easyfxml.model.exception.ExceptionHandler.displayExceptionPane;

@Component
public class TwitterInterractionService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterInterractionService.class);

    private final SessionManager sessionManager;

    public TwitterInterractionService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public <T> T interract(final T target, final TwitterBinaryInterraction<T> twitterBinaryInterraction) {
        if (twitterBinaryInterraction.shouldDo().apply(this, target)) {
            return twitterBinaryInterraction.onTrue().apply(this, target);
        } else {
            return twitterBinaryInterraction.onFalse().apply(this, target);
        }
    }

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

    public boolean notYetLiked(final Status tweet) {
        return !sessionManager.doWithCurrentTwitter(twitter -> twitter.showStatus(tweet.getId()).isFavorited())
                              .get();
    }

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

    Status unretweet(final Status tweet) {
        return sessionManager.doWithCurrentTwitter(twitter -> {
            final long retweetId = twitter.showStatus(tweet.getId()).getCurrentUserRetweetId();
            return twitter.destroyStatus(retweetId);
        }).onSuccess(resultingStatus -> LOG.debug(
                "User {} unretweeted tweet {}",
                getCurrentScreenName(),
                resultingStatus.getId()
        )).onFailure(err -> displayExceptionPane(
                "Could not unretweet tweet!",
                err.getMessage(),
                err
        )).get();
    }

    public boolean notYetRetweeted(final Status tweet) {
        return !sessionManager.doWithCurrentTwitter(twitter -> twitter.showStatus(tweet.getId()).isRetweetedByMe())
                              .get();
    }

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

    public boolean notYetFollowed(final User user) {
        return !sessionManager.doWithCurrentTwitter(twitter -> twitter.showFriendship(
                getCurrentScreenName(),
                user.getScreenName()
        )).map(Relationship::isSourceFollowingTarget).get();
    }

    private String getCurrentScreenName() {
        return sessionManager.getCurrentTwitter()
                             .mapTry(Twitter::getScreenName)
                             .getOrElseThrow(err -> new IllegalStateException("Current user unavailable!", err));
    }

}
