package moe.lyrebird.view.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javaslang.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Tristan on 06/02/2017.
 */
@Component
@Slf4j
@Data
public class EasyFXML {
    private final FXMLLoader fxmlLoader;
    
    @Autowired
    public EasyFXML(final FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
    
    /**
     * This is a wrapper around {@link #getPaneForFile(String)} for views inside
     * {@link moe.lyrebird.view.views} package.
     *
     * @param fileName
     *         The raw file name (e.g. RootView.fxml)
     * @return the pane associated
     */
    public Try<Pane> getPaneForView(final String fileName) {
        return this.getPaneForFile(Views.VIEWS_ROOT_FOLDER + fileName);
    }
    
    /**
     * Loads the pane associated to a given file path safely.
     *
     * @param filePathString
     *         The path to the file (e.g. /some/file/on/the/system
     *         or moe/lyrebird/file/on/classpath)
     * @return the {@link Pane} associated to it.
     */
    public Try<Pane> getPaneForFile(final String filePathString) {
        this.fxmlLoader.setLocation(getURLForView(filePathString));
        try {
            final Pane filePane = this.fxmlLoader.load();
            return Try.of(() -> filePane);
        } catch (final IOException e) {
            log.error("Could not locate file at path : "+filePathString, e);
            return Try.failure(e);
        }
    }
    
    public static URL getURLForView(final String filePathString) {
        return EasyFXML.class.getClassLoader().getResource(filePathString);
    }
}
