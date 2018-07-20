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
import java.util.concurrent.Executor;

@Component
public class CleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupService.class);

    private final Executor cleanupExecutor;
    private final Queue<CleanupOperation> onShutdownHooks;

    public CleanupService(@Qualifier("cleanupExecutor") final Executor cleanupExecutor) {
        this.cleanupExecutor = cleanupExecutor;
        this.onShutdownHooks = new LinkedList<>();
    }

    public void registerCleanupOperation(final CleanupOperation cleanupOperation) {
        LOG.debug("Registering cleanup operation : {}", cleanupOperation.getName());
        onShutdownHooks.add(cleanupOperation);
    }

    public void executeCleanupOperations() {
        cleanupExecutor.execute(() -> {
            LOG.debug("Executing cleanup hooks !");
            onShutdownHooks.forEach(this::executeCleanupOperationWithTimeout);
            LOG.debug("All cleanup hooks have been executed!");
        });
    }

    private void executeCleanupOperationWithTimeout(final CleanupOperation cleanupOperation) {
        LOG.debug("\t-> {}", cleanupOperation.getName());
        try {
            new Thread(cleanupOperation.getOperation()).join(5000);
        } catch (InterruptedException e) {
            LOG.error("The cleanup operation thread for {} was interrupted! Skipping!", e);
        } catch (final RuntimeException e) {
            LOG.error("An uncaught exception was thrown in a cleanup task!", e);
            e.printStackTrace();
        }
    }

}
