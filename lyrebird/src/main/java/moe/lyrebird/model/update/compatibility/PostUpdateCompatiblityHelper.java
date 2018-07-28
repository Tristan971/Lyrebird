package moe.lyrebird.model.update.compatibility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class PostUpdateCompatiblityHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PostUpdateCompatiblityHelper.class);

    private static final PostUpdateCompatiblityHelper INSTANCE = new PostUpdateCompatiblityHelper();

    private final Map<String, Runnable> oneTimeExecutions = new HashMap<>();

    public static PostUpdateCompatiblityHelper getInstance() {
        return INSTANCE;
    }

    private PostUpdateCompatiblityHelper() {
        LOG.debug("Initialized post-update compatibility helper.");
    }

    public void executeCompatibilityTasks() {
        LOG.debug("Parsing post-update compatibility tasks.");
        Stream.of(wipeUserData())
              .forEach(PostUpdateCompatibilityTask::executeIfNecessary);
    }

    private PostUpdateCompatibilityTask wipeUserData() {
        final List<String> reasons = Collections.singletonList(
                "1.1.2-twitter4j-to-twitter4a-hibernate"
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
