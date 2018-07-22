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

package moe.lyrebird.view.components.tweet;

import org.ocpsoft.prettytime.PrettyTime;
import twitter4j.Status;

import java.util.Date;

/**
 * Small formatting utils for display of tweets (aka {@link Status} on Twitter4J's side).
 */
final class TweetFormatter {

    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    private TweetFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    static String time(final Status status) {
        final Date tweetDate = status.getCreatedAt();
        return PRETTY_TIME.format(tweetDate);
    }

}
