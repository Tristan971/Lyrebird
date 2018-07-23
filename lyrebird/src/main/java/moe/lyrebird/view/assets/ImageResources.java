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

    BACKGROUND_DARK_1PX("background-dark-1px.png"),

    CONTROLBAR_ADD_USER("controlbar_icon_add_user.png"),

    GENERAL_USER_AVATAR_DARK("general_icon_user_avatar_dark.png"),
    GENERAL_USER_AVATAR_LIGHT("general_icon_user_avatar_light.png"),
    GENERAL_LOADING_REMOTE("general_icon_loading_remote.png"),

    TWEETPANE_LIKE_OFF("tweetpane_icon_heart_off.png"),
    TWEETPANE_LIKE_ON("tweetpane_icon_heart_on.png"),
    TWEETPANE_VIDEO("tweetpane_icon_video.png");

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
