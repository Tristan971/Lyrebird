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

package moe.lyrebird.model.update.selfupdate;

import org.springframework.stereotype.Component;
import io.vavr.control.Option;
import moe.lyrebird.api.model.TargetPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;

/**
 * This service helps choose the right package type for the current platform.
 */
@Component
public class BinaryChoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryChoiceService.class);

    boolean currentPlatformSupportsSelfupdate() {
        return detectRunningPlatform().isDefined();
    }

    /**
     * Maps the current platform with a Lyrebird package type platform.
     *
     * @return the appropriate {@link TargetPlatform} for the current platform if supported. If the current platform is
     * not specifically supported, returns {@link Option#none()}.
     */
    Option<TargetPlatform> detectRunningPlatform() {
        LOG.debug("Detecting platform...");
        switch (SystemInfo.getCurrentPlatformEnum()) {
            case WINDOWS:
                LOG.debug("Running on Windows");
                return Option.of(TargetPlatform.WINDOWS);
            case LINUX:
                LOG.debug("Running on Linux.");
                return Option.none();
            case MACOSX:
                LOG.debug("Running on macOS.");
                return Option.of(TargetPlatform.MACOS);
            case SOLARIS:
            case FREEBSD:
            case UNKNOWN:
            default:
                LOG.debug("Unknown platform : {}", SystemInfo.getCurrentPlatformEnum());
                return Option.none();
        }
    }

}
