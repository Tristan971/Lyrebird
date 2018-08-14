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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.refresh.RateLimited;
import moe.lyrebird.model.twitter.services.interraction.TwitterInteractionService;

import twitter4a.Paging;
import twitter4a.Status;
import twitter4a.Twitter;
import twitter4a.TwitterException;

/**
 * This class exposes the current user's timeline in an observable way
 */
@Lazy
@Component
public class Timeline extends TwitterTimelineBaseModel implements RateLimited {

    private static final Logger LOG = LoggerFactory.getLogger(Timeline.class);

    private final TwitterInteractionService interactionService;

    @Autowired
    public Timeline(
            final SessionManager sessionManager,
            final TwitterInteractionService interactionService
    ) {
        super(sessionManager);
        this.interactionService = interactionService;
    }

    @Override
    protected List<Status> initialLoad(final Twitter twitter) throws TwitterException {
        return twitter.getHomeTimeline()
                      .stream()
                      .filter(((Predicate<Status>) interactionService::isRetweetByCurrentUser).negate())
                      .collect(Collectors.toList());
    }

    @Override
    protected List<Status> backfillLoad(final Twitter twitter, final Paging paging) throws TwitterException {
        return twitter.getHomeTimeline(paging)
                      .stream()
                      .filter(((Predicate<Status>) interactionService::isRetweetByCurrentUser).negate())
                      .collect(Collectors.toList());
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

    @Override
    public int maxRequestsPer15Minutes() {
        return 15;
    }

}
