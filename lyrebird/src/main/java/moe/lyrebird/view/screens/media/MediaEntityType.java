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

package moe.lyrebird.view.screens.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.MediaEntity;

import java.util.Arrays;

public enum MediaEntityType {
    PHOTO("photo"),
    ANIMATED_GIF("animated_gif"),
    VIDEO("video"),
    UNMANAGED("<UNMANAGED_TYPE>");

    private static final Logger LOG = LoggerFactory.getLogger(MediaEntityType.class);

    private final String codeName;

    MediaEntityType(final String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

    public static MediaEntityType fromTwitterType(final String actualCode) {
        return Arrays.stream(values())
                .filter(type -> type.getCodeName().equals(actualCode))
                .findAny()
                .orElse(UNMANAGED);
    }

    public static boolean isSupported(final MediaEntity entity) {
        final boolean supported = fromTwitterType(entity.getType()) != UNMANAGED;
        if (!supported) {
            LOG.warn("Unsupported twitter media entity, skipping. Twitter type was : [{}]", entity.getType());
        }
        return supported;
    }

}
