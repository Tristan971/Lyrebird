package moe.lyrebird.model.update.compatibility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

final class PostUpdateCompatibilityTask {

    private static final Logger LOG = LoggerFactory.getLogger(PostUpdateCompatibilityTask.class);

    private static final Preferences USER_PREFERENCES = Preferences.userRoot().node("Lyrebird");

    private final String name;
    private final List<String> reasonsToExecute;
    private final Runnable execution;

    PostUpdateCompatibilityTask(final String name, final List<String> reasonsToExecute, final Runnable execution) {
        this.name = name;
        this.reasonsToExecute = reasonsToExecute;
        this.execution = execution;
    }

    void executeIfNecessary() {
        LOG.debug("-> {}", name);
        LOG.debug("Checking for the following reasons {}", reasonsToExecute);
        final List<String> unfulfilled = reasonsToExecute(reasonsToExecute);

        if (unfulfilled.isEmpty()) {
            LOG.debug("No need for execution.");
        } else {
            LOG.debug("Need to execute {} due to {}", name, unfulfilled);
            execution.run();
            LOG.debug("Executed! Setting reasons {} to fulfilled.", unfulfilled);
            unfulfilled.forEach(reason -> USER_PREFERENCES.putBoolean(reason, true));
        }
    }

    private static List<String> reasonsToExecute(final List<String> reasonsToCheck) {
        return reasonsToCheck.stream()
                             .filter(reason -> !USER_PREFERENCES.getBoolean(reason, false))
                             .collect(Collectors.toList());
    }
}
