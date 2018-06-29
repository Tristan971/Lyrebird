package moe.lyrebird.view.components.timeline;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.Timeline;
import moe.lyrebird.view.components.TimelineBasedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TimelineController extends TimelineBasedController {

    private static final Logger LOG = LoggerFactory.getLogger(TimelineController.class);

    public TimelineController(
            final Timeline timeline,
            final SessionManager sessionManager,
            final ConfigurableApplicationContext context
    ) {
        super(timeline, sessionManager, context);
    }

    @Override
    protected Logger LOG() {
        return LOG;
    }

}
