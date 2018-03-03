package moe.lyrebird.view.views;

import moe.tristan.easyfxml.api.FxmlStylesheet;
import moe.tristan.easyfxml.util.Resources;

import java.net.URL;

public enum Styles implements FxmlStylesheet {
    LYREBIRD("lyrebird.css");

    private final String resourceRelativeLocation;

    Styles(final String resourceRelativeLocation) {
        this.resourceRelativeLocation = resourceRelativeLocation;
    }


    @Override
    public String getExternalForm() {
        return Resources.getResourceURL(resourceRelativeLocation).map(URL::toExternalForm).get();
    }

}
