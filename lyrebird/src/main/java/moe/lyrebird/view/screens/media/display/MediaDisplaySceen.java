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
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.lyrebird.view.screens.media.MediaScreenController;
import moe.lyrebird.view.screens.media.display.photo.PhotoScreenController;
import moe.lyrebird.view.screens.media.display.video.VideoScreenController;

public enum MediaDisplaySceen implements FxmlNode {
    PHOTO("photo/PhotoScreen.fxml", PhotoScreenController.class),
    VIDEO("video/VideoScreen.fxml", VideoScreenController.class);

    private final String file;
    private final Class<? extends MediaScreenController> mediaScreenController;

    MediaDisplaySceen(String file, Class<? extends MediaScreenController> mediaScreenController) {
        this.file = file;
        this.mediaScreenController = mediaScreenController;
    }

    @Override
    public FxmlFile getFile() {
        return () -> "moe/lyrebird/view/screens/media/display/" + file;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return mediaScreenController;
    }
}
