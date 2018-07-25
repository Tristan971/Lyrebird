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

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.DirectMessageEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.CollectionUtils.toMultiValueMap;

@Component
public class DirectMessages {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessages.class);

    private final SessionManager sessionManager;
    private final ObservableMap<Long, List<DirectMessageEvent>> directMessagesMap;

    public DirectMessages(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        LOG.debug("Initializing direct messages manager.");
        this.directMessagesMap = FXCollections.observableMap(toMultiValueMap(new ConcurrentHashMap<>()));
    }

    public ObservableMap<Long, List<DirectMessageEvent>> loadedConversations() {
        return FXCollections.unmodifiableObservableMap(directMessagesMap);
    }

    public void loadDirectMessages() {
        loadDirectMessages(50);
    }

    public void loadDirectMessages(final int count) {
        LOG.debug("Requesting last {} direct messages.", count);
        sessionManager.getCurrentTwitter()
                      .mapTry(twitter -> twitter.getDirectMessageEvents(count))
                      .onSuccess(messages -> messages.forEach(this::addDirectMessage))
                      .onFailure(err -> LOG.error("Could not load direct messages successfully!", err));
    }

    private void addDirectMessage(final DirectMessageEvent directMessage) {
        final long sender = directMessage.getSenderId();
        if (!directMessagesMap.keySet().contains(sender)) {
            directMessagesMap.put(sender, FXCollections.observableArrayList());
        }

        final List<DirectMessageEvent> messagesFromSender = directMessagesMap.get(sender);
        messagesFromSender.add(directMessage);
        messagesFromSender.sort(Comparator.comparingLong(DirectMessageEvent::getId).reversed());
    }

}
