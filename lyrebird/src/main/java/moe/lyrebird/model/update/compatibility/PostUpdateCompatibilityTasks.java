package moe.lyrebird.model.update.compatibility;

import java.io.File;
import java.util.List;

public enum PostUpdateCompatibilityTasks implements PostUpdateCompatibilityTask {
    WIPE_DB(
            "Wipe Lyrebird database",
            List.of("1.1.2-twitter4j-to-twitter4a-hibernate", "1.1.4-twitter4a-to-twitter4j-hibernate"),
            () -> deleteIfExists(".lyrebird")
    );

    private final String name;
    private final List<String> reasonsToExecute;
    private final Runnable execution;

    PostUpdateCompatibilityTasks(String name, List<String> reasonsToExecute, Runnable execution) {
        this.name = name;
        this.reasonsToExecute = reasonsToExecute;
        this.execution = execution;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getReasonsToExecute() {
        return reasonsToExecute;
    }

    @Override
    public Runnable getExecution() {
        return execution;
    }

    private static void deleteIfExists(final String pathRelativeToUserHome) {
        final File toDelete = new File(System.getProperty("user.home"), pathRelativeToUserHome);
        if (toDelete.exists()) {
            try {
                assert toDelete.delete() : "Could not delete " + toDelete.getAbsolutePath() + " for compatibility!";
            } catch (AssertionError error) {
                throw new IllegalStateException(error);
            }
        }
    }

}
