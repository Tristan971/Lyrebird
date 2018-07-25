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
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DirectMessages {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessages.class);

    private final SessionManager sessionManager;
    private final ObservableMap<User, ObservableList<DirectMessageEvent>> messageEvents;

    public DirectMessages(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        LOG.debug("Initializing direct messages manager.");
        this.messageEvents = FXCollections.observableMap(new ConcurrentHashMap<>());
    }

    public ObservableMap<User, ObservableList<DirectMessageEvent>> loadedConversations() {
        return messageEvents;
    }

    public void refresh() {
        CompletableFuture.runAsync(() -> {
            LOG.debug("Requesting last {} direct messages.", 50);
            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> twitter.getDirectMessageEvents(50))
                          .onSuccess(messages -> messages.forEach(this::addDirectMessage))
                          .onFailure(err -> LOG.error("Could not load direct messages successfully!", err));
        });
    }

    private void addDirectMessage(final DirectMessageEvent directMessage) {
        final long senderId = directMessage.getSenderId();
        final User sender = messageEvents.keySet()
                                                 .stream()
                                                 .filter(user -> user.getId() == senderId)
                                                 .findAny()
                                                 .orElseGet(() -> showUser(senderId));

        messageEvents.computeIfAbsent(sender, __ -> FXCollections.observableArrayList());

        final List<DirectMessageEvent> messagesFromSender = messageEvents.get(sender);
        messagesFromSender.add(directMessage);
        messagesFromSender.sort(Comparator.comparingLong(DirectMessageEvent::getId));
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
