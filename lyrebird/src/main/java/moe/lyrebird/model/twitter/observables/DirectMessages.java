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

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.refresh.RateLimited;

import twitter4j.DirectMessage;
import twitter4j.User;

@Component
public class DirectMessages implements RateLimited {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessages.class);

    private final SessionManager sessionManager;
    private final ObservableMap<User, ObservableList<DirectMessage>> messageEvents;

    public DirectMessages(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        LOG.debug("Initializing direct messages manager.");
        this.messageEvents = FXCollections.observableHashMap();
    }

    public ObservableMap<User, ObservableList<DirectMessage>> directMessages() {
        return messageEvents;
    }

    @Override
    public void refresh() {
        if (!sessionManager.isLoggedInProperty().getValue()) {
            LOG.debug("Logged out, not refreshing direct messages.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            LOG.debug("Requesting last direct messages.");
            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> twitter.getDirectMessages(20))
                          .onSuccess(this::addDirectMessages)
                          .onFailure(err -> LOG.error("Could not load direct messages successfully!", err));
        });
    }

    @Override
    public int maxRequestsPer15Minutes() {
        return 15;
    }

    private void addDirectMessages(final List<DirectMessage> loadedMessages) {
        final var knownMessages = messageEvents.values()
                                               .stream()
                                               .flatMap(List::stream)
                                               .collect(Collectors.toList());

        final var newMessages = loadedMessages.stream()
                                              .filter(((Predicate<DirectMessage>) knownMessages::contains).negate())
                                              .collect(Collectors.toList());
        LOG.debug("Loaded {} new messages", newMessages.size());

        newMessages.forEach(this::addDirectMessage);
    }

    public void addDirectMessage(final DirectMessage directMessageEvent) {
        final long otherId = getOtherId(directMessageEvent);
        final User other = messageEvents.keySet()
                                        .stream()
                                        .filter(user -> user.getId() == otherId)
                                        .findAny()
                                        .orElseGet(() -> showUser(otherId));

        final ObservableList<DirectMessage> messagesWithOther = messageEvents.computeIfAbsent(
                other,
                k -> FXCollections.observableArrayList()
        );
        messagesWithOther.add(directMessageEvent);
        messagesWithOther.sort(Comparator.comparingLong(DirectMessage::getId));
    }

    private long getOtherId(final DirectMessage directMessageEvent) {
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
