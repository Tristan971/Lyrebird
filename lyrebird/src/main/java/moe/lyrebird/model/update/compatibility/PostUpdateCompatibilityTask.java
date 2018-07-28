package moe.lyrebird.model.update.compatibility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public final class PostUpdateCompatibilityTask {

    private static final Logger LOG = LoggerFactory.getLogger(PostUpdateCompatibilityTask.class);

    private static final Preferences USER_PREFS = Preferences.userRoot().node("Lyrebird");

    private final String name;
    private final List<String> reasonsToExecute;
    private final Runnable execution;

    PostUpdateCompatibilityTask(final String name, final List<String> reasonsToExecute, final Runnable execution) {
        this.name = name;
        this.reasonsToExecute = reasonsToExecute;
        this.execution = execution;
    }

    public void executeIfNecessary() {
        LOG.debug("-> {}", name);
        LOG.debug("Checking for the following reasons {}", reasonsToExecute);
        final List<String> unfullfilled = reasonsToExecute(reasonsToExecute);

        if (unfullfilled.isEmpty()) {
            LOG.debug("No need for execution.");
        } else {
            LOG.debug("Need to execute {} due to {}", name, unfullfilled);
            execution.run();
            LOG.debug("Executed! Setting reasons {} to fullilled.", unfullfilled);
            unfullfilled.forEach(reason -> USER_PREFS.putBoolean(reason, true));
        }
    }

    private static List<String> reasonsToExecute(final List<String> reasonsToCheck) {
        return reasonsToCheck.stream()
                             .filter(reason -> !USER_PREFS.getBoolean(reason, false))
                             .collect(Collectors.toList());
    }
}
