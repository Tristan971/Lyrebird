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

package moe.lyrebird.view.components.mentions;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.Mentions;
import moe.lyrebird.view.components.TimelineBasedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MentionsController extends TimelineBasedController {

    private static final Logger LOG = LoggerFactory.getLogger(MentionsController.class);

    public MentionsController(
            final Mentions mentions,
            final SessionManager sessionManager,
            final ConfigurableApplicationContext context
    ) {
        super(mentions, sessionManager, context, true);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

}
