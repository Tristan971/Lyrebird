package moe.lyrebird.model.twitter.observables;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

@Component
public class Mentions extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(Mentions.class);

    public Mentions(SessionManager sessionManager) {
        super(sessionManager, Twitter::getMentionsTimeline, Twitter::getMentionsTimeline);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

}
