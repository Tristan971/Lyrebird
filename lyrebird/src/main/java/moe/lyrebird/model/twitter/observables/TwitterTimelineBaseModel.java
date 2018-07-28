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
import twitter4a.Paging;
import twitter4a.Status;
import twitter4a.Twitter;
import twitter4a.TwitterException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is the base class for reverse-chronologically sorted tweet lists (aka Timelines) backend model.
 */
public abstract class TwitterTimelineBaseModel {

    protected final SessionManager sessionManager;

    private final ObservableList<Status> loadedTweets = FXCollections.observableList(new LinkedList<>());

    public TwitterTimelineBaseModel(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * @return The currently loaded tweets.
     */
    public ObservableList<Status> loadedTweets() {
        return FXCollections.unmodifiableObservableList(loadedTweets);
    }

    /**
     * Asynchronously requests loading of tweets prior to the given status.
     *
     * @param loadUntilThisStatus the status whose prior tweets are requested
     */
    public void loadMoreTweets(final long loadUntilThisStatus) {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting more tweets.");
            final Paging requestPaging = new Paging();
            requestPaging.setMaxId(loadUntilThisStatus);

            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> backfillLoad(twitter, requestPaging))
                          .onSuccess(this::addTweets);
            getLocalLogger().debug("Finished loading more tweets.");
        });
    }

    /**
     * Asynchronously loads the last tweets available
     */
    public void loadLastTweets() {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting last tweets in timeline.");
            sessionManager.getCurrentTwitter()
                          .mapTry(this::initialLoad)
                          .onSuccess(this::addTweets);
        });
    }

    /**
     * Add a given list of tweets to the currently loaded ones.
     *
     * @param receivedTweets The tweets to add.
     */
    private void addTweets(final List<Status> receivedTweets) {
        receivedTweets.forEach(this::addTweet);
        getLocalLogger().debug("Loaded {} tweets successfully.", receivedTweets.size());
    }

    /**
     * Adds a single tweet to the list of currently loaded ones.
     *
     * @param newTweet The tweet to add.
     */
    public void addTweet(final Status newTweet) {
        if (!this.loadedTweets.contains(newTweet)) {
            this.loadedTweets.add(newTweet);
            this.loadedTweets.sort(Comparator.comparingLong(Status::getId).reversed());
        }
    }

    /**
     * Removes a given tweet from the list of currently loaded ones.
     *
     * @param removedId The id of the tweet to remove
     *
     * @see Status#getId()
     */
    public void removeTweet(final long removedId) {
        this.loadedTweets.stream()
                         .filter(status -> status.getId() == removedId)
                         .findFirst()
                         .ifPresent(this.loadedTweets::remove);
    }

    /**
     * Remove all loaded tweets.
     */
    void clearLoadedTweets() {
        loadedTweets.clear();
    }

    /**
     * Performs the initial load of tweets (i.e. {@link #loadLastTweets()}).
     *
     * @param twitter The twitter instance to use
     *
     * @return The list of tweets received from Twitter
     * @throws TwitterException if there was an issue loading tweets
     */
    protected abstract List<Status> initialLoad(final Twitter twitter)
    throws TwitterException;

    /**
     * Performs a request for loading more tweets (i.e. {@link #loadMoreTweets(long)}).
     *
     * @param twitter The twitter instance to use
     * @param paging  Parameters for the request (containing the tweet whose prior tweets are requested for example)
     *
     * @return The list of tweets received from Twitter
     * @throws TwitterException if there was an issue loading tweets
     */
    protected abstract List<Status> backfillLoad(final Twitter twitter, final Paging paging)
    throws TwitterException;

    /**
     * @return The subclass' logger that will be used for logging requests and potential errors
     */
    protected abstract Logger getLocalLogger();

}
