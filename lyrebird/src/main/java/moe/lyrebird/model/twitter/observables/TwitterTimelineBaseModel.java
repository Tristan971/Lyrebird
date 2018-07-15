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
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class TwitterTimelineBaseModel {

    protected final SessionManager sessionManager;
    private final Executor twitterExecutor;

    private final ObservableList<Status> loadedTweets = FXCollections.observableList(new LinkedList<>());

    public TwitterTimelineBaseModel(
            final SessionManager sessionManager,
            final Executor twitterExecutor
    ) {
        this.twitterExecutor = twitterExecutor;
        this.sessionManager = sessionManager;
    }

    public ObservableList<Status> loadedTweets() {
        return FXCollections.unmodifiableObservableList(loadedTweets);
    }

    public void loadMoreTweets(final long loadUntilThisStatus) {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting more tweets.");
            final Paging requestPaging = new Paging();
            requestPaging.setMaxId(loadUntilThisStatus);

            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> backfillLoad(twitter, requestPaging))
                          .onSuccess(this::addTweets);
            getLocalLogger().debug("Finished loading more tweets.");
        }, twitterExecutor);
    }

    public void loadLastTweets() {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting last tweets in timeline.");
            sessionManager.getCurrentTwitter()
                          .mapTry(this::initialLoad)
                          .onSuccess(this::addTweets);
        }, twitterExecutor);
    }

    private void addTweets(final List<Status> receivedTweets) {
        receivedTweets.forEach(this::addTweet);
        getLocalLogger().debug("Loaded {} tweets successfully.", receivedTweets.size());
    }

    public void addTweet(final Status newTweet) {
        if (!this.loadedTweets.contains(newTweet)) {
            this.loadedTweets.add(newTweet);
            this.loadedTweets.sort(Comparator.comparingLong(Status::getId).reversed());
        }
    }

    public void removeTweet(final long removedId) {
        this.loadedTweets.stream()
                         .filter(status -> status.getId() == removedId)
                         .findFirst()
                         .ifPresent(this.loadedTweets::remove);
    }

    public void clearLoadedTweets() {
        loadedTweets.clear();
    }

    protected abstract ResponseList<Status> initialLoad(final Twitter twitter)
    throws TwitterException;

    protected abstract ResponseList<Status> backfillLoad(final Twitter twitter, final Paging paging)
    throws TwitterException;

    protected abstract Logger getLocalLogger();
    
}
