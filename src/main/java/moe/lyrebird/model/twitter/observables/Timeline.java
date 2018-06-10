package moe.lyrebird.model.twitter.observables;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

@Component
public class Timeline extends TwitterTimelineBaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(Timeline.class);

    public Timeline(final SessionManager sessionManager) {
        super(sessionManager, Twitter::getHomeTimeline, Twitter::getHomeTimeline);
    }

    @Override
    protected Logger getLocalLogger() {
        return LOG;
    }

}
