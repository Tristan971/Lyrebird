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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.notifications.Notification;
import moe.lyrebird.model.notifications.NotificationService;
import moe.lyrebird.model.notifications.format.TwitterNotifications;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.refresh.RateLimited;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * This class exposes the current user's mentions in an observable way
 */
@Lazy
@Component
public class Mentions extends TwitterTimelineBaseModel implements RateLimited {

    private static final Logger LOG = LoggerFactory.getLogger(Mentions.class);

    private final NotificationService notificationService;

    public Mentions(final SessionManager sessionManager, final NotificationService notificationService) {
        super(sessionManager);
        this.notificationService = notificationService;
    }

    @Override
    protected ResponseList<Status> initialLoad(final Twitter twitter) throws TwitterException {
        return twitter.getMentionsTimeline();
    }

    @Override
    protected ResponseList<Status> backfillLoad(final Twitter twitter, final Paging paging) throws TwitterException {
        return twitter.getMentionsTimeline(paging);
    }

    @Override
    protected void onNewElementStreamed(final Status newElement) {
        final Notification mentionNotification = TwitterNotifications.fromMention(newElement);
        notificationService.sendNotification(mentionNotification);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

    @Override
    public int maxRequestsPer15Minutes() {
        return 75;
    }

}
