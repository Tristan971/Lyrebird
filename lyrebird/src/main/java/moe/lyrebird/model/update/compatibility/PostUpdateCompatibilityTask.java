package moe.lyrebird.model.update.compatibility;

import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;

interface PostUpdateCompatibilityTask {

    Logger LOG = LoggerFactory.getLogger(PostUpdateCompatibilityTask.class);
    Preferences USER_PREFERENCES = Preferences.userRoot().node("Lyrebird");

    String getName();

    List<String> getReasonsToExecute();

    Runnable getExecution();

    default Option<Runnable> getRequiredExecution() {
        LOG.debug("-> {}", getName());
        LOG.debug("\tChecking for the following reasons {}", getReasonsToExecute());
        final List<String> unfulfilled = getUnfulfilled(getReasonsToExecute());

        if (unfulfilled.isEmpty()) {
            return Option.of(() -> LOG.debug(
                    "\tNo need for execution of {} as reasons all fulfilled : {}",
                    getName(),
                    getReasonsToExecute()
            ));
        } else {
            return Option.of(() -> {
                LOG.debug("\tNeed to execute {} due to {}", getName(), unfulfilled);
                getExecution().run();
                LOG.debug("\tExecuted! Setting reasons {} to fulfilled.", unfulfilled);
                unfulfilled.forEach(reason -> USER_PREFERENCES.putBoolean(reason, true));
            });
        }
    }

    private static List<String> getUnfulfilled(final List<String> reasonsToCheck) {
        return reasonsToCheck.stream()
                             .filter(reason -> !USER_PREFERENCES.getBoolean(reason, false))
                             .collect(Collectors.toList());
    }
}
