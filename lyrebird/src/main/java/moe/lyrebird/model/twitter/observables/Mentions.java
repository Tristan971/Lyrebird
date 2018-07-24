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

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Paging;
import twitter4a.ResponseList;
import twitter4a.Status;
import twitter4a.Twitter;
import twitter4a.TwitterException;
import twitter4a.UserMentionEntity;

import java.util.Arrays;

/**
 * This class exposes the current user's mentions in an observable way
 */
@Lazy
@Component
public class Mentions extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(Mentions.class);

    public Mentions(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected ResponseList<Status> initialLoad(final Twitter twitter) throws TwitterException {
        return twitter.getMentionsTimeline();
    }

    @Override
    protected ResponseList<Status> backfillLoad(final Twitter twitter, final Paging paging) throws TwitterException {
        return twitter.getMentionsTimeline(paging);
    }

    /**
     * @param status the status to test against
     *
     * @return whether a given status is a mention to the current user
     */
    public boolean isMentionToCurrentUser(final Status status) {
        final Session currentSession = sessionManager.currentSessionProperty().getValue();
        if (currentSession == null) {
            return false;
        }
        return Arrays.stream(status.getUserMentionEntities())
                     .map(UserMentionEntity::getId)
                     .map(String::valueOf)
                     .anyMatch(currentSession.getUserId()::equals);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

}
