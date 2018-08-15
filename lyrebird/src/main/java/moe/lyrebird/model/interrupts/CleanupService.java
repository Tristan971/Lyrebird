/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.interrupts;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This service is called at shutdown to execute a certain amount of cleanup operations.
 */
@Component
public class CleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupService.class);

    private static final Executor CLEANUP_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Queue<CleanupOperation> onShutdownHooks = new LinkedList<>();

    /**
     * Registers a cleanup operation for execution at shutdown.
     *
     * @param cleanupOperation The operation to execute at shutdown
     */
    public void registerCleanupOperation(final CleanupOperation cleanupOperation) {
        CompletableFuture.runAsync(() -> {
            LOG.debug("Registering cleanup operation : {}", cleanupOperation.getName());
            onShutdownHooks.add(cleanupOperation);
        }, CLEANUP_EXECUTOR);
    }

    /**
     * Executes the cleanup operations that were previously registered via
     * {@link #registerCleanupOperation(CleanupOperation)}.
     */
    public void executeCleanupOperations() {
        CLEANUP_EXECUTOR.execute(() -> {
            LOG.info("Cleaning up...");
            LOG.debug("Executing cleanup hooks:");
            onShutdownHooks.forEach(CleanupService::executeCleanupOperationWithTimeout);
            LOG.debug("All cleanup hooks have been executed!");
        });
    }

    /**
     * This method executes a single cleanup operation with a timeout. We are not systemd that gives 1m30s for a cleanup
     * operation so you get 5 seconds to do whatever it is you need.
     *
     * @param cleanupOperation The operation to execute
     */
    private static void executeCleanupOperationWithTimeout(final CleanupOperation cleanupOperation) {
        LOG.debug("\t-> {}", cleanupOperation.getName());
        cleanupOperation.getOperation().run();
    }

}
