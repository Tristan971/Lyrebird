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

package moe.lyrebird.model.twitter.observables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Paging;
import twitter4a.ResponseList;
import twitter4a.Status;
import twitter4a.Twitter;
import twitter4a.TwitterException;
import twitter4a.User;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.Executor;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * This class exposes a user's self timeline in an observable way
 */
@Lazy
@Component
@Scope(SCOPE_PROTOTYPE)
public class UserTimeline extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(UserTimeline.class);

    private final Property<User> targetUser = new SimpleObjectProperty<>(null);

    @Autowired
    public UserTimeline(final SessionManager sessionManager, final Executor twitterExecutor) {
        super(sessionManager, twitterExecutor);
        this.targetUser.addListener((o, prev, cur) -> {
            this.clearLoadedTweets();
            loadLastTweets();
        });
    }

    /**
     * @return the {@link Property} for the user whose self timeline we are interested in
     */
    public Property<User> targetUserProperty() {
        return targetUser;
    }

    @Override
    protected ResponseList<Status> initialLoad(final Twitter twitter) throws TwitterException {
        return twitter.getUserTimeline(targetUser.getValue().getId());
    }

    @Override
    protected ResponseList<Status> backfillLoad(final Twitter twitter, final Paging paging) throws TwitterException {
        return twitter.getUserTimeline(targetUser.getValue().getId(), paging);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

}
