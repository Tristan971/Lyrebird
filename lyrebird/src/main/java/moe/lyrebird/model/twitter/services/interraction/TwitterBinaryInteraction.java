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

package moe.lyrebird.model.twitter.services.interraction;

import java.util.function.BiFunction;

/**
 * A binary interaction is a twitter operation that can be executed in two ways depending on a third information.
 * @param <T> The type over which to execute these operations
 */
public interface TwitterBinaryInteraction<T> {

    /**
     * @return the operation to execute if {@link #shouldDo()} returns true.
     */
    BiFunction<TwitterInteractionService, T, T> onTrue();

    /**
     * @return the operation to execute if {@link #shouldDo()} returns false.
     */
    BiFunction<TwitterInteractionService, T, T> onFalse();

    /**
     * @return whether the {@link #onTrue()} is the relevant one.
     */
    BiFunction<TwitterInteractionService, T, Boolean> shouldDo();

}
