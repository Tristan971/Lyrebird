package moe.lyrebird.model.threading;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Application-wide background task execution manager
 */
@Slf4j
public final class BackgroundService implements ScheduledExecutorService {

    @Delegate
    private final ScheduledExecutorService executorService;
    
    private BackgroundService() {
        this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

}
