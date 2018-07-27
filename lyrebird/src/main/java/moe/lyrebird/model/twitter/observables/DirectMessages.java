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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.DirectMessageEvent;
import twitter4a.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DirectMessages {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessages.class);

    private final SessionManager sessionManager;
    private final ObservableMap<User, ObservableList<DirectMessageEvent>> messageEvents;

    public DirectMessages(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        LOG.debug("Initializing direct messages manager.");
        this.messageEvents = FXCollections.observableHashMap();
        refresh();
    }

    public ObservableMap<User, ObservableList<DirectMessageEvent>> directMessages() {
        return messageEvents;
    }

    public void refresh() {
        CompletableFuture.runAsync(() -> {
            LOG.debug("Requesting last direct messages.");
            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> twitter.getDirectMessageEvents(20))
                          .onSuccess(this::addDirectMessages)
                          .onFailure(err -> LOG.error("Could not load direct messages successfully!", err));
        });
    }

    private void addDirectMessages(final List<DirectMessageEvent> loadedMessages) {
        var knownMessages = messageEvents.values()
                                         .stream()
                                         .flatMap(List::stream)
                                         .collect(Collectors.toList());

        var newMessages = loadedMessages.stream()
                                        .filter(((Predicate<DirectMessageEvent>) knownMessages::contains).negate())
                                        .collect(Collectors.toList());
        LOG.debug("Loaded {} new messages", newMessages.size());

        newMessages.forEach(this::addDirectMessage);
    }

    public void addDirectMessage(final DirectMessageEvent directMessageEvent) {
        final long otherId = getOtherId(directMessageEvent);
        final User other = messageEvents.keySet()
                                        .stream()
                                        .filter(user -> user.getId() == otherId)
                                        .findAny()
                                        .orElseGet(() -> showUser(otherId));

        final ObservableList<DirectMessageEvent> messagesWithOther = messageEvents.computeIfAbsent(
                other,
                k -> FXCollections.observableArrayList()
        );
        messagesWithOther.add(directMessageEvent);
        messagesWithOther.sort(Comparator.comparingLong(DirectMessageEvent::getId));
    }

    private long getOtherId(final DirectMessageEvent directMessageEvent) {
        return sessionManager.isCurrentUser(directMessageEvent.getSenderId()) ?
               directMessageEvent.getRecipientId() :
               directMessageEvent.getSenderId();
    }

    @Cacheable(value = "userFromUserId", sync = true)
    public User showUser(final long userId) {
        return sessionManager.doWithCurrentTwitter(
                twitter -> twitter.showUser(userId)
        ).getOrElseThrow(
                err -> new IllegalStateException("Cannot find user with id " + userId, err)
        );
    }

}
