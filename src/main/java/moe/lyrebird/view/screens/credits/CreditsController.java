package moe.lyrebird.view.screens.credits;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;

import org.assertj.core.util.Sets;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.util.Set;

import static org.springframework.util.MimeTypeUtils.TEXT_HTML_VALUE;

@Component
public class CreditsController implements FxmlController {

    @FXML
    private VBox creditsVBox;

    @Override
    public void initialize() {
        findAllCreditsPages().stream()
                        .map(html -> {
                            final WebView webView = new WebView();
                            webView.getEngine().loadContent(html, TEXT_HTML_VALUE);
                            return webView;
                        }).forEach(creditsVBox.getChildren()::add);
    }

    public Set<String> findAllCreditsPages() {
        return Sets.newTreeSet("TEST");
    }

}
