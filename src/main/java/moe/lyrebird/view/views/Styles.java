package moe.lyrebird.view.views;

import moe.tristan.easyfxml.api.FxmlStylesheet;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum Styles implements FxmlStylesheet {
    LYREBIRD("lyrebird.css");

    private final String resourceRelativeLocation;

    Styles(final String resourceRelativeLocation) {
        this.resourceRelativeLocation = resourceRelativeLocation;
    }

    @Override
    public Path getPath() {
        final URL res = Thread.currentThread().getContextClassLoader().getResource(resourceRelativeLocation);
        assert res != null;
        try {
            return Paths.get(res.toURI());
        } catch (final URISyntaxException e) {
            return null;
        }
    }

}
