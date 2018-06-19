package moe.lyrebird.view.components;

import javafx.scene.image.Image;

public enum ImageResources {

    BLANK_USER_PROFILE_PICTURE("user_avatar_icon.svg"),
    BLANK_USER_PROFILE_PICTURE_LIGHT("user_avatar_icon_light.svg");

    private final Image backingImage;

    ImageResources(final String path) {
        this.backingImage = loadImage(path);
    }

    private static Image loadImage(final String path) {
        final ClassLoader cl = ImageResources.class.getClassLoader();
        final String finalPath = "assets/img/" + path;

        return new Image(cl.getResourceAsStream(finalPath));
    }

    public Image getImage() {
        return backingImage;
    }

}
