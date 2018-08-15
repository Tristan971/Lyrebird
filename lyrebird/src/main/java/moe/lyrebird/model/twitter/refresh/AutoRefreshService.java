package moe.lyrebird.model.twitter.refresh;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.util.FXProperties;

@Component
public class AutoRefreshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRefreshService.class);

    private static final ScheduledExecutorService REFRESHER = Executors.newSingleThreadScheduledExecutor();

    private final List<RateLimited> rateLimitedCalls;

    @Autowired
    public AutoRefreshService(final SessionManager sessionManager, final List<RateLimited> rateLimitedCalls) {
        this.rateLimitedCalls = rateLimitedCalls;
        LOGGER.debug("Loaded the following autorefresh calls {}", rateLimitedCalls);
        FXProperties.waitForBooleanProp(sessionManager.isLoggedInProperty(), this::startAutoRefreshing);
    }

    private void startAutoRefreshing() {
        LOGGER.debug("Starting autorefreshes...");
        rateLimitedCalls.forEach(rateLimitedCall -> {
            final int secondsBetweenCalls = secondsBetweenCalls(rateLimitedCall);
            REFRESHER.scheduleAtFixedRate(() -> {
                try {
                    rateLimitedCall.refresh();
                } catch (final Exception e) {
                    stopAutoRefreshing();
                }
            }, 2, secondsBetweenCalls, TimeUnit.SECONDS);
            LOGGER.debug("Scheduled autorefresh {} every {} seconds", rateLimitedCall, secondsBetweenCalls);
        });
    }

    private void stopAutoRefreshing() {
        final List<Runnable> stopped = REFRESHER.shutdownNow();
        LOGGER.debug("Stopped autorefreshing for all calls [{}] : {}", rateLimitedCalls, stopped);
    }

    private static int secondsBetweenCalls(final RateLimited rateLimited) {
        final double secBetweenCalls = (15.0 * 60.0) / ((double) rateLimited.maxRequestsPer15Minutes() * 0.8);
        return (int) Math.max(3, secBetweenCalls);
    }

}
