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

public enum Interration implements TweetInterraction {
    LIKE(
            TweetInterractionService::like,
            TweetInterractionService::unlike,
            TweetInterractionService::shouldLike
    ),
    RETWEET(
            TweetInterractionService::retweet,
            TweetInterractionService::unretweet,
            TweetInterractionService::shouldRetweet
    );

    private final BiFunction<TweetInterractionService, Status, Status> onTrue;
    private final BiFunction<TweetInterractionService, Status, Status> onFalse;
    private final BiFunction<TweetInterractionService, Status, Boolean> shouldDo;

    Interration(
            final BiFunction<TweetInterractionService, Status, Status> onTrue,
            final BiFunction<TweetInterractionService, Status, Status> onFalse,
            final BiFunction<TweetInterractionService, Status, Boolean> shouldDo
    ) {
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        this.shouldDo = shouldDo;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Status> onTrue() {
        return onTrue;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Status> onFalse() {
        return onFalse;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Boolean> shouldDo() {
        return shouldDo;
    }
}
