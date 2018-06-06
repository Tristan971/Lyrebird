package moe.lyrebird.model.twitter.observables;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import io.vavr.Predicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.DirectMessage;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DirectMessages {

    private static final Logger LOG = LoggerFactory.getLogger(DirectMessages.class);

    private final MultiValueMap<User, DirectMessage> directMessages;

    public DirectMessages() {
        LOG.debug("Initializing direct messages manager.");
        this.directMessages = CollectionUtils.toMultiValueMap(new ConcurrentHashMap<>());
    }

    public void addDirectMessage(final DirectMessage directMessage) {
        final User sender = directMessage.getSender();
        if (!directMessages.keySet().contains(sender)) {
            directMessages.put(sender, new ArrayList<>());
        }

        final List<DirectMessage> messagesFromSender = directMessages.get(sender);
        messagesFromSender.add(directMessage);
        messagesFromSender.sort(Comparator.comparingLong(DirectMessage::getId).reversed());
    }

    public void removeDirectMessage(final long senderId, final long directMessageId) {
        directMessages.keySet()
                      .stream()
                      .filter(user -> user.getId() == senderId)
                      .findAny()
                      .map(directMessages::get)
                      .filter(Predicates.noneOf(Objects::isNull, List::isEmpty))
                      .ifPresent(dmList ->
                                         dmList.stream()
                                               .filter(dm -> dm.getId() == directMessageId)
                                               .findAny()
                                               .ifPresent(dmList::remove)
                      );
    }

}
