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

import io.vavr.CheckedFunction1;
import io.vavr.CheckedFunction2;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class TwitterTimelineBaseModel {

    private final SessionManager sessionManager;
    
    private final CheckedFunction1<Twitter, ResponseList<Status>> initialLoadCall;
    private final CheckedFunction2<Twitter, Paging, ResponseList<Status>> backFillCall;

    private final ObservableList<Status> loaded = FXCollections.observableList(new LinkedList<>());

    public TwitterTimelineBaseModel(
            final SessionManager sessionManager,
            final CheckedFunction1<Twitter, ResponseList<Status>> initialLoadCall,
            final CheckedFunction2<Twitter, Paging, ResponseList<Status>> backFillCall
    ) {
        getLocalLogger().info("Initializing tweet timeline model for type : {}", initialLoadCall);
        this.sessionManager = sessionManager;
        this.initialLoadCall = initialLoadCall;
        this.backFillCall = backFillCall;
    }

    public ObservableList<Status> loadedTweets() {
        return FXCollections.unmodifiableObservableList(loaded);
    }

    public void loadMoreTweets(final long loadUntilThisStatus) {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting more tweets.");
            final Paging requestPaging = new Paging();
            requestPaging.setMaxId(loadUntilThisStatus);

            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> backFillCall.apply(twitter, requestPaging))
                          .onSuccess(this::addTweets);
            getLocalLogger().debug("Finished loading more tweets.");
        });
    }

    public void loadLastTweets() {
        CompletableFuture.runAsync(() -> {
            getLocalLogger().debug("Requesting last tweets in timeline.");
            sessionManager.getCurrentTwitter()
                          .mapTry(initialLoadCall)
                          .onSuccess(this::addTweets);
        });
    }

    private void addTweets(final List<Status> receivedTweets) {
        receivedTweets.forEach(this::addTweet);
        getLocalLogger().debug("Loaded {} tweets successfully.", receivedTweets.size());
    }

    public void addTweet(final Status newTweet) {
        if (!this.loaded.contains(newTweet)) {
            this.loaded.add(newTweet);
            this.loaded.sort(Comparator.comparingLong(Status::getId).reversed());
        }
    }

    public void removeTweet(final long removedId) {
        this.loaded.stream()
                         .filter(status -> status.getId() == removedId)
                         .findFirst()
                         .ifPresent(this.loaded::remove);
    }
    
    protected abstract Logger getLocalLogger();
    
}
