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

import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.beans.property.Property;

import java.util.concurrent.Executor;

public class UserTimeline extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(UserTimeline.class);

    public UserTimeline(
            final SessionManager sessionManager,
            final Executor twitterExecutor,
            final Property<User> targetUser
    ) {
        super(
                sessionManager,
                twitterExecutor,
                twitter -> twitter.getUserTimeline(targetUser.getValue().getId()),
                (twitter, paging) -> twitter.getUserTimeline(targetUser.getValue().getId(), paging)
        );
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }
}
