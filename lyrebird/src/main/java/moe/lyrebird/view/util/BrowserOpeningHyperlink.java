package moe.lyrebird.view.util;

import javafx.scene.control.Hyperlink;

import java.util.function.Consumer;

public class BrowserOpeningHyperlink extends Hyperlink {

    private String currentURL;

    public BrowserOpeningHyperlink(final Consumer<String> onClicked) {
        super();
        setOnAction(e -> onClicked.accept(currentURL));
    }

    public BrowserOpeningHyperlink withTarget(final String url) {
        setText(url);
        this.currentURL = url;
        return this;
    }

}
