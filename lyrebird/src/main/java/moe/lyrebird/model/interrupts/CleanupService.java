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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class CleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupService.class);

    private final Executor cleanupExecutor;
    private final Queue<CleanupOperation> onShutdownHooks;

    public CleanupService(@Qualifier("cleanupExecutor") final Executor cleanupExecutor) {
        this.cleanupExecutor = cleanupExecutor;
        onShutdownHooks = new LinkedList<>();
    }

    public void registerCleanupOperation(final CleanupOperation cleanupOperation) {
        LOG.debug("Registering cleanup operation : {}", cleanupOperation.getName());
        onShutdownHooks.add(cleanupOperation);
    }

    public void executeCleanupOperations() {
        LOG.debug("Executing cleanup hooks !");
        cleanupExecutor.execute(() -> onShutdownHooks.forEach(this::executeCleanupOperationWithTimeout));
        LOG.debug("All cleanup hooks have been executed!");
    }

    private void executeCleanupOperationWithTimeout(final CleanupOperation cleanupOperation) {
        LOG.debug("\t-> {}", cleanupOperation.getName());
        try {
            CompletableFuture.runAsync(cleanupOperation.getOperation(), cleanupExecutor)
                             .get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Could not actually call the following hook [{}] !", cleanupOperation.getName(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOG.error("The hook [{}] encountered an exception while executing!", cleanupOperation.getName(), e);
        } catch (TimeoutException e) {
            LOG.error("The hook [{}] could not finish in the given time!", cleanupOperation.getName(), e);
        }
    }

}
