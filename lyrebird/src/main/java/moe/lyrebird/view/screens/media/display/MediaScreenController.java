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

package moe.lyrebird.view.screens.media.display;

import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.view.util.StageAware;

import java.net.URL;

/**
 * Simple interface for controllers for individual media display.
 */
public interface MediaScreenController extends FxmlController, StageAware {

    /**
     * Loads the given media and displays it appropriately given the implementation and media type.
     *
     * @param mediaUrl The URL of the media type as a String. Always use {@link URL#toExternalForm()} for it if you have
     *                 a URL-type object to begin with.
     */
    void handleMedia(final String mediaUrl);

    /**
     * Enforces media controllers to think about properly siszing their stage to fit as well as possible the embedded
     * media.
     */
    void bindViewSizeToParent();

}
