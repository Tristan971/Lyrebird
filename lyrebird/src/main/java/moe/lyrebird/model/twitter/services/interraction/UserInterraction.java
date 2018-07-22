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

import twitter4j.User;

import java.util.function.BiFunction;

/**
 * Convenience enumeration for {@link TwitterBinaryInterraction}s which apply to {@link User}s.
 */
public enum UserInterraction implements TwitterBinaryInterraction<User> {

    FOLLOW(
            TwitterInterractionService::follow,
            TwitterInterractionService::unfollow,
            TwitterInterractionService::notYetFollowed
    );

    private final BiFunction<TwitterInterractionService, User, User> onTrue;
    private final BiFunction<TwitterInterractionService, User, User> onFalse;
    private final BiFunction<TwitterInterractionService, User, Boolean> shouldDo;

    UserInterraction(
            final BiFunction<TwitterInterractionService, User, User> onTrue,
            final BiFunction<TwitterInterractionService, User, User> onFalse,
            final BiFunction<TwitterInterractionService, User, Boolean> shouldDo
    ) {
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        this.shouldDo = shouldDo;
    }

    @Override
    public BiFunction<TwitterInterractionService, User, User> onTrue() {
        return onTrue;
    }

    @Override
    public BiFunction<TwitterInterractionService, User, User> onFalse() {
        return onFalse;
    }

    @Override
    public BiFunction<TwitterInterractionService, User, Boolean> shouldDo() {
        return shouldDo;
    }

}
