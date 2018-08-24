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

package moe.lyrebird.view.screens.media.handlers.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.MediaEntity;

import java.util.Arrays;

/**
 * Contains matchers for the currently known media entity types from Twitter.
 */
public enum TwitterMediaEntity {
    PHOTO("photo"),
    ANIMATED_GIF("animated_gif"),
    VIDEO("video"),
    UNMANAGED("<UNMANAGED_TYPE>");

    private static final Logger LOG = LoggerFactory.getLogger(TwitterMediaEntity.class);

    private final String codeName;

    TwitterMediaEntity(final String codeName) {
        this.codeName = codeName;
    }

    /**
     * @return The Twitter-side entity type code.
     */
    public String getCodeName() {
        return codeName;
    }

    /**
     * Maps a given Twitter-side {@link MediaEntity} to a Lyrebird-side {@link TwitterMediaEntity}.
     *
     * @param actualCode The twitter code of a {@link MediaEntity}.
     *
     * @return The matching Lyrebird-side entity or {@link #UNMANAGED}.
     */
    public static TwitterMediaEntity fromTwitterType(final String actualCode) {
        return Arrays.stream(values())
                     .filter(type -> type.getCodeName().equals(actualCode))
                     .findAny()
                     .orElse(UNMANAGED);
    }

    /**
     * Tests whether a given entity has an associated embedding mapping inside of Lyrebird.
     *
     * @param entity The entity to test
     *
     * @return true if and only if calling {@link #fromTwitterType(String)} does not yield {@link #UNMANAGED}.
     */
    public static boolean isSupported(final MediaEntity entity) {
        final boolean supported = fromTwitterType(entity.getType()) != UNMANAGED;
        if (!supported) {
            LOG.warn("Unsupported twitter media entity, skipping. Twitter type was : [{}]", entity.getType());
        }
        return supported;
    }

}
