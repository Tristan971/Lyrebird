package moe.lyrebird.view.screens.credits;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.TEXT_HTML_VALUE;

@Component
public class CreditsController implements FxmlController {

    private final PathMatchingResourcePatternResolver pmrpr;

    @FXML
    private VBox creditsVBox;

    public CreditsController() {
        this.pmrpr = new PathMatchingResourcePatternResolver();
    }

    @Override
    public void initialize() {
        findAllCreditsPages().stream()
                             .map(html -> {
                                 final WebView webView = new WebView();
                                 webView.getEngine().loadContent(html, TEXT_HTML_VALUE);
                                 return webView;
                             }).forEach(creditsVBox.getChildren()::add);
    }

    @Cacheable("creditsComponents")
    public List<String> findAllCreditsPages() {
        try {
            final Resource[] credits = pmrpr.getResources("classpath:assets/credits/*");
            final List<String> loadedCredits = new ArrayList<>(credits.length);

            for (final Resource credit: credits) {
                final File creditResFile = credit.getFile();
                final InputStream is = new FileInputStream(creditResFile);
                final String content = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
                loadedCredits.add(content);
            }

            return loadedCredits;
        } catch (final IOException e) {
            ExceptionHandler.displayExceptionPane(
                    "Error loading credits",
                    "",
                    e
            );
            return Collections.emptyList();
        }
    }

}
