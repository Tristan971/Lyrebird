package moe.lyrebird.model.update.compatibility;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PostUpdateCompatibilityHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PostUpdateCompatibilityHelper.class);

    private PostUpdateCompatibilityHelper() {
    }

    public static void executeCompatibilityTasks() {
        LOG.debug("Parsing post-update compatibility tasks.");
        Stream.of(wipeUserData())
              .forEach(PostUpdateCompatibilityTask::executeIfNecessary);
    }

    private static PostUpdateCompatibilityTask wipeUserData() {
        final List<String> reasons = Collections.singletonList(
                "1.1.2-twitter4j-to-twitter4j-hibernate"
        );

        final Runnable execution = () -> {
            final File userSettingsFolder = new File(System.getProperty("user.home"), ".lyrebird");
            if (userSettingsFolder.exists() && userSettingsFolder.isDirectory()) {
                Arrays.stream(Objects.requireNonNull(userSettingsFolder.listFiles())).forEach(File::delete);
            }
        };

        return new PostUpdateCompatibilityTask("Wipe user data.", reasons, execution);
    }

}
