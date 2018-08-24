package moe.lyrebird.model.twitter.services;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.DirectMessages;

import twitter4j.DirectMessage;
import twitter4j.User;

@Component
public class NewDirectMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(NewDirectMessageService.class);

    private final SessionManager sessionManager;
    private final DirectMessages directMessages;

    @Autowired
    public NewDirectMessageService(final SessionManager sessionManager, final DirectMessages directMessages) {
        this.sessionManager = sessionManager;
        this.directMessages = directMessages;
    }

    public CompletableFuture<DirectMessage> sendMessage(final User recipient, final String content) {
        LOG.debug("Sending direct message to [{}] with content {}", recipient.getScreenName(), content);
        return CompletableFuture.supplyAsync(() -> sessionManager.doWithCurrentTwitter(
                twitter -> twitter.sendDirectMessage(recipient.getId(), content))
        ).thenApplyAsync(
                msgReq -> msgReq.onSuccess(dme -> {
                    LOG.debug("Sent direct message! {}", dme);
                    directMessages.addDirectMessage(dme);
                }).onFailure(
                        err -> LOG.error("Could not send direct message!", err)
                ).getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new)
        ).whenCompleteAsync((res, err) -> {
            if (err != null) {
                LOG.error("Could not correctly executed the direct message sending request!", err);
            }
        });
    }

}
