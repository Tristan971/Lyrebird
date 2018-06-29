package moe.lyrebird.view.screens;

import moe.tristan.easyfxml.api.FxmlStylesheet;
import moe.tristan.easyfxml.util.Resources;

import java.nio.file.Path;

public enum Styles implements FxmlStylesheet {
    LYREBIRD("style/lyrebird.css");

    private final String resourceRelativeLocation;

    Styles(final String resourceRelativeLocation) {
        this.resourceRelativeLocation = resourceRelativeLocation;
    }

    @Override
    public Path getPath() {
        return Resources.getResourcePath(resourceRelativeLocation).get();
    }

}
