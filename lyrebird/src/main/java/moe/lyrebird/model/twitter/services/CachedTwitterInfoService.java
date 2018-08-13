package moe.lyrebird.model.twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import twitter4a.User;

@Component
@Cacheable(value = "cachedTwitterInfo", sync = true)
public class CachedTwitterInfoService {

    private final SessionManager sessionManager;

    @Autowired
    public CachedTwitterInfoService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public User getUser(final long userId) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.showUser(userId)).getOrNull();
    }

}

