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

import twitter4j.Status;

import java.util.function.BiFunction;

public enum StatusInterraction implements TwitterBinaryInterraction<Status> {
    LIKE(
            TwitterInterractionService::like,
            TwitterInterractionService::unlike,
            TwitterInterractionService::notYetLiked
    ),
    RETWEET(
            TwitterInterractionService::retweet,
            TwitterInterractionService::unretweet,
            TwitterInterractionService::notYetRetweeted
    );

    private final BiFunction<TwitterInterractionService, Status, Status> onTrue;
    private final BiFunction<TwitterInterractionService, Status, Status> onFalse;
    private final BiFunction<TwitterInterractionService, Status, Boolean> shouldDo;

    StatusInterraction(
            final BiFunction<TwitterInterractionService, Status, Status> onTrue,
            final BiFunction<TwitterInterractionService, Status, Status> onFalse,
            final BiFunction<TwitterInterractionService, Status, Boolean> shouldDo
    ) {
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        this.shouldDo = shouldDo;
    }

    @Override
    public BiFunction<TwitterInterractionService, Status, Status> onTrue() {
        return onTrue;
    }

    @Override
    public BiFunction<TwitterInterractionService, Status, Status> onFalse() {
        return onFalse;
    }

    @Override
    public BiFunction<TwitterInterractionService, Status, Boolean> shouldDo() {
        return shouldDo;
    }
}
