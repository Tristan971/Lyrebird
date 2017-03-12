package moe.lyrebird.system;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manages all the cleanup tasks of the application.
 */
@Slf4j
public class CleanupService {
    private final ExecutorService executorService;
    private final Queue<Runnable> cleanupTasks;
    
    public CleanupService() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.cleanupTasks = new LinkedList<>();
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUp, "CleanUp"));
    }
    
    public void registerCleanupTask(final Runnable task) {
        this.cleanupTasks.add(task);
    }
    
    private void cleanUp() {
        this.cleanupTasks.forEach(this.executorService::submit);
    }
}
