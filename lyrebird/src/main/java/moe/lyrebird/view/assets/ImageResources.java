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

package moe.lyrebird.view.assets;

import javafx.scene.image.Image;

/**
 * This enumetation contains some static images used either as placeholders or fallbacks.
 */
public enum ImageResources {

    ADD_USER_PROFILE_PICTURE("add_user.png"),
    BACKGROUND_DARK_1PX("background-dark-1px.png"),
    BLANK_USER_PROFILE_PICTURE("user_avatar_icon.png"),
    BLANK_USER_PROFILE_PICTURE_LIGHT("user_avatar_icon_light.png"),
    LOADING_REMOTE("loading_remote.png"),
    VIDEO_PLAYER("video_player.png");

    private final Image backingImage;

    /**
     * @param path The path of the resource relative to src/main/resources/assets/img
     */
    ImageResources(final String path) {
        this.backingImage = loadImage(path);
    }

    /**
     * This method is the load call executed on constructor call to preload the images on startup.
     *
     * @param path The path from the enum member declaration.
     *
     * @return The underlying image that will actually get used.
     */
    private static Image loadImage(final String path) {
        final ClassLoader cl = ImageResources.class.getClassLoader();
        final String finalPath = "assets/img/" + path;

        return new Image(cl.getResourceAsStream(finalPath));
    }

    public Image getImage() {
        return backingImage;
    }

}
