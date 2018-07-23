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

/**
 * A cleanup operation consists of a name and an operation which are respectively logged and executed during the
 * application's shutdown process.
 */
public final class CleanupOperation {

    private final String name;
    private final Runnable operation;

    /**
     * @param name      The human-readable name that will be logged for this operation
     * @param operation The actual operation to execute at shutdown time
     */
    public CleanupOperation(final String name, final Runnable operation) {
        this.name = name;
        this.operation = operation;
    }

    String getName() {
        return name;
    }

    Runnable getOperation() {
        return operation;
    }

}
