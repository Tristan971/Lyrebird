package moe.lyrebird.model.twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import twitter4a.User;

@Component
public class TwitterInformationService {

    private final SessionManager sessionManager;

    @Autowired
    public TwitterInformationService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Cacheable("showUser")
    public User getUser(final long userId) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.showUser(userId)).getOrNull();
    }

}
