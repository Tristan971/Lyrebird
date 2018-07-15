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
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.Executor;

@Component
public class UserTimeline extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(UserTimeline.class);

    private final Property<User> targetUser;

    @Autowired
    public UserTimeline(final SessionManager sessionManager, final Executor twitterExecutor) {
        super(sessionManager, twitterExecutor);
        targetUser = new SimpleObjectProperty<>(null);
    }

    public void setTargetUser(User targetUser) {
        this.targetUser.setValue(targetUser);
    }

    @Override
    protected ResponseList<Status> initialLoad(Twitter twitter) throws TwitterException {
        return twitter.getUserTimeline(targetUser.getValue().getId());
    }

    @Override
    protected ResponseList<Status> backfillLoad(Twitter twitter, Paging paging) throws TwitterException {
        return twitter.getUserTimeline(targetUser.getValue().getId(), paging);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }
}
