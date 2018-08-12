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

package moe.lyrebird.view.components.base;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.TwitterTimelineBaseModel;
import moe.lyrebird.view.components.cells.TweetListCell;
import moe.lyrebird.view.components.mentions.MentionsController;
import moe.lyrebird.view.components.timeline.TimelineController;
import moe.tristan.easyfxml.model.components.listview.ComponentListViewFxmlController;

import twitter4a.Status;

/**
 * This class serves as a base implementation for backend observing lists of chronologically reverse-sorted tweets (aka
 * a timeline).
 *
 * @see TimelineController
 * @see MentionsController
 * @see TimelineControllerBase
 */
public abstract class TimelineControllerBase extends ComponentListViewFxmlController<Status> {

    private final TwitterTimelineBaseModel timelineBase;
    private final ListProperty<Status> tweetsProperty;
    private final boolean shouldAutomaticallyFill;

    /**
     * This constructor is called by the implementing class rather than Spring because there is no way we will ever use
     * field injection in Lyrebird.
     *
     * @param timelineBase            The backing model-side controller taking care of requests and exposing tweets
     * @param sessionManager          The session manager is used only for bootstrapping and does not leak outside of
     *                                constructor
     * @param context                 The spring context that is passed onto {@link ComponentListViewFxmlController} for
     *                                constructor injection.
     * @param shouldAutomaticallyFill Whether this controller should directly start fetching tweets on creation.
     */
    public TimelineControllerBase(
            final TwitterTimelineBaseModel timelineBase,
            final SessionManager sessionManager,
            final ConfigurableApplicationContext context,
            final boolean shouldAutomaticallyFill
    ) {
        super(context, TweetListCell.class);
        this.timelineBase = timelineBase;
        this.tweetsProperty = new ReadOnlyListWrapper<>(timelineBase.loadedTweets());
        this.shouldAutomaticallyFill = shouldAutomaticallyFill;
        sessionManager.currentSessionProperty().addListener(change -> timelineBase.loadLastTweets());
    }

    @Override
    public void initialize() {
        super.initialize();
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(timelineBase.loadedTweets()));
        if (shouldAutomaticallyFill) {
            timelineBase.loadLastTweets();
        }
    }

    /**
     * Requests loading of older tweets.
     */
    private void loadMoreTweets() {
        getOldestTweetLoaded().ifPresent(oldestStatus -> {
            getLogger().debug("Loading tweets before {}", oldestStatus.getId());
            timelineBase.loadMoreTweets(oldestStatus.getId());
            listView.scrollTo(oldestStatus);
        });
    }

    /**
     * @return The previously oldest loaded tweets for use in {@link #loadMoreTweets()}.
     */
    private Optional<Status> getOldestTweetLoaded() {
        if (tweetsProperty.isEmpty()) {
            getLogger().debug("No older tweets to load.");
            return Optional.empty();
        }
        final Status oldest = tweetsProperty.getValue().get(tweetsProperty.size() - 1);
        getLogger().debug("Loading tweets before {}", oldest.getId());
        return Optional.of(oldest);
    }

    /**
     * Called when the user scrolls to the end of {@link #listView}.
     */
    @Override
    protected void onScrolledToEndOfListView() {
        loadMoreTweets();
    }

    /**
     * @return The logger to use for calls from the child class.
     */
    protected abstract Logger getLogger();

}
