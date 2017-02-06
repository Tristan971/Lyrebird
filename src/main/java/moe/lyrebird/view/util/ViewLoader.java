package moe.lyrebird.view.util;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import moe.lyrebird.view.views.ErrorPane;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for taking a request for a
 * view and returning a JavaFX node for it, whether as a
 * scene for main views (windows) or as a Pane for subviews.
 */
public class ViewLoader {
    
    private final EasyFXML easyFXML;
    
    @Autowired
    public ViewLoader(final EasyFXML easyFXML) {
        this.easyFXML = easyFXML;
    }
    
    /**
     * Wrapper around {@link #loadPane(String)} that puts the returning {@link Pane}
     * into a {@link Scene}.
     *
     * @param windowName
     *         The name of the window to load
     * @return The {@link Scene} containing the loaded {@link Pane}
     */
    public Scene loadWindow(final String windowName) {
        final Pane effectivePane = this.loadPane(windowName);
        return new Scene(effectivePane);
    }
    
    /**
     * Loads a pane from a file. The file is assumed to be a view.
     * i.e. a standard FXML file inside {@link moe.lyrebird.view.views}.
     *
     * @param paneName
     *         The pane's filename to load (i.e. "RootView.fxml")
     * @return The {@link Pane} corresponding to the filename.
     */
    public Pane loadPane(final String paneName) {
        return this.easyFXML.getPaneForView(paneName)
                .getOrElseGet(
                        failure -> ErrorPane.of("Could not load window : " + paneName, failure)
                );
    }
}
