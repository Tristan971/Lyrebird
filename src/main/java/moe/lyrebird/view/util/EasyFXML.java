package moe.lyrebird.view.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javaslang.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;

import static moe.lyrebird.view.views.Views.VIEWS_ROOT_FOLDER;

/**
 * Created by Tristan on 06/02/2017.
 */
@Slf4j
@Data
public class EasyFXML {
    
    private final ApplicationContext context;
    
    @Autowired
    public EasyFXML(final ApplicationContext context) {
        this.context = context;
    }
    
    /**
     * This is a wrapper around {@link #getPaneForFile(String)} for views inside
     * {@link Views} enum.
     *
     * @param view
     *         The view to load
     * @return the pane associated
     */
    public Try<Pane> getPaneForView(final Views view) {
        return this.getPaneForFile(VIEWS_ROOT_FOLDER.toString() + view.toString());
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
        final FXMLLoader loader = this.context.getBean(FXMLLoader.class);
        loader.setLocation(getURLForView(filePathString));
        try {
            final Pane filePane = loader.load();
            return Try.of(() -> filePane);
        } catch (final IOException e) {
            log.error("Could not locate file at path : " + filePathString, e);
            return Try.failure(e);
        }
    }
    
    public static URL getURLForView(final String filePathString) {
        return EasyFXML.class.getClassLoader().getResource(filePathString);
    }
}
