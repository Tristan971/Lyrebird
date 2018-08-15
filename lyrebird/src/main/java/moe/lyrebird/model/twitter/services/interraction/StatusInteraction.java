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

import twitter4a.Status;

import java.util.function.BiFunction;

/**
 * Convenience enumeration for {@link TwitterBinaryInteraction}s which apply to {@link Status}es.
 */
public enum StatusInteraction implements TwitterBinaryInteraction<Status> {

    LIKE(
            TwitterInteractionService::like,
            TwitterInteractionService::unlike,
            TwitterInteractionService::notYetLiked
    ),
    RETWEET(
            TwitterInteractionService::retweet,
            TwitterInteractionService::unretweet,
            TwitterInteractionService::notYetRetweeted
    );

    private final BiFunction<TwitterInteractionService, Status, Status> onTrue;
    private final BiFunction<TwitterInteractionService, Status, Status> onFalse;
    private final BiFunction<TwitterInteractionService, Status, Boolean> shouldDo;

    StatusInteraction(
            final BiFunction<TwitterInteractionService, Status, Status> onTrue,
            final BiFunction<TwitterInteractionService, Status, Status> onFalse,
            final BiFunction<TwitterInteractionService, Status, Boolean> shouldDo
    ) {
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        this.shouldDo = shouldDo;
    }

    @Override
    public BiFunction<TwitterInteractionService, Status, Status> onTrue() {
        return onTrue;
    }

    @Override
    public BiFunction<TwitterInteractionService, Status, Status> onFalse() {
        return onFalse;
    }

    @Override
    public BiFunction<TwitterInteractionService, Status, Boolean> shouldDo() {
        return shouldDo;
    }

}
