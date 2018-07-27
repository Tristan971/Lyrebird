package moe.lyrebird.model.twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.DirectMessages;
import twitter4a.MessageData;
import twitter4a.User;

@Component
public class NewDirectMessageService {

    private final SessionManager sessionManager;
    private final DirectMessages directMessages;

    @Autowired
    public NewDirectMessageService(
            final SessionManager sessionManager,
            final DirectMessages directMessages
    ) {
        this.sessionManager = sessionManager;
        this.directMessages = directMessages;
    }

    public void sendMessage(final User recipient, final String content) {
        sessionManager.doWithCurrentTwitter(
                twitter -> twitter.createMessage(buildMessage(recipient, content))
        ).onSuccess(directMessages::addDirectMessage);
    }

    private MessageData buildMessage(final User recipient, final String content) {
        return new MessageData(recipient.getId(), content);
    }

}
