package moe.lyrebird.view.components.mentions;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.observables.Mentions;
import moe.lyrebird.view.components.TimelineBasedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MentionsController extends TimelineBasedController {

    private static final Logger LOG = LoggerFactory.getLogger(MentionsController.class);

    public MentionsController(
            final Mentions mentions,
            final SessionManager sessionManager,
            final ApplicationContext context
    ) {
        super(mentions, sessionManager, context);
    }

    @Override
    protected Logger LOG() {
        return LOG;
    }

}
