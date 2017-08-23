package moe.lyrebird.view.util;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.PathUtils;
import moe.lyrebird.view.views.ErrorPane;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This class is responsible for taking a request for a
 * view and returning a JavaFX node for it, whether as a
 * scene for main views (windows) or as a Pane for subviews.
 */
@Slf4j
public class ViewLoader {
    private static final Path CUSTOM_CSS_PATH = Paths.get("./theme");
    
    private final EasyFXML easyFXML;
    
    @Autowired
    public ViewLoader(final EasyFXML easyFXML) {
        this.easyFXML = easyFXML;
    }
    
    /**
     * Loads a pane from a file. The file is assumed to be a view.
     * i.e. a standard FXML file inside {@link moe.lyrebird.view.views}.
     *
     * @param view
     *         The view to load
     * @return The {@link Pane} corresponding to the filename.
     */
    public Pane loadPane(final Views view) {
        log.debug("Loading Pane at path {}", view.name());
    
        return this.easyFXML.getPaneForView(view)
                .getOrElseGet(
                        failure -> ErrorPane.of("Could not load window : " + view, failure)
                );
    }
    
    public Scene getRootScene() {
        final Pane rootPane = this.loadPane(Views.ROOT_VIEW);
        return new Scene(rootPane);
    }
    
    private static Optional<String> getDefaultStyleSheet() {
        final Path pathToCssFile = PathUtils.getPathForResource("lyrebird.css");
        try {
            return Optional.of(pathToCssFile.toUri().toURL().toString());
        } catch (final MalformedURLException e) {
            log.error("Could not load CSS file", e);
            return Optional.empty();
        }
    }
}
