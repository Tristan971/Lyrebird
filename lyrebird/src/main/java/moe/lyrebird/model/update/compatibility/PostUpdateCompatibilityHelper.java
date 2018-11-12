package moe.lyrebird.model.update.compatibility;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;

public final class PostUpdateCompatibilityHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PostUpdateCompatibilityHelper.class);

    private PostUpdateCompatibilityHelper() {
    }

    public static void executeCompatibilityTasks() {
        LOG.debug("Parsing post-update compatibility tasks.");
        Arrays.stream(PostUpdateCompatibilityTasks.values())
              .map(PostUpdateCompatibilityTask::getRequiredExecution)
              .flatMap(Option::toJavaStream)
              .forEach(Runnable::run);
    }

}
