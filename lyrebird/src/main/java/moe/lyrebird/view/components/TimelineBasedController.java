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

package moe.lyrebird.view.components;

import org.springframework.context.ConfigurableApplicationContext;
import moe.tristan.easyfxml.model.components.listview.ComponentListViewFxmlController;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.TwitterTimelineBaseModel;
import moe.lyrebird.view.components.cells.TweetListCell;
import org.slf4j.Logger;
import twitter4j.Status;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;

import java.util.Optional;

public abstract class TimelineBasedController extends ComponentListViewFxmlController<Status> {

    private final TwitterTimelineBaseModel timelineBase;
    private final ListProperty<Status> tweetsProperty;
    private final boolean shouldAutomaticallyFill;

    public TimelineBasedController(
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

    private void loadMoreTweets() {
        getOldestTweetLoaded().ifPresent(oldestStatus -> {
            getLogger().debug("Loading tweets before {}", oldestStatus.getId());
            timelineBase.loadMoreTweets(oldestStatus.getId());
            listView.scrollTo(oldestStatus);
        });
    }

    private Optional<Status> getOldestTweetLoaded() {
        if (tweetsProperty.isEmpty()) {
            getLogger().debug("No older tweets to load.");
            return Optional.empty();
        }
        final Status oldest = tweetsProperty.getValue().get(tweetsProperty.size() - 1);
        getLogger().debug("Loading tweets before {}", oldest.getId());
        return Optional.of(oldest);
    }

    @Override
    protected void onScrolledToEndOfListView() {
        loadMoreTweets();
    }

    protected abstract Logger getLogger();

}
