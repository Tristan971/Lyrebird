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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import java.util.concurrent.Executor;

@Lazy
@Component
public class Mentions extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(Mentions.class);

    public Mentions(final SessionManager sessionManager, @Qualifier("twitterExecutor") final Executor twitterExecutor) {
        super(sessionManager, twitterExecutor, Twitter::getMentionsTimeline, Twitter::getMentionsTimeline);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

}
