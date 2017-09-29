package moe.lyrebird.system;

import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.model.threading.BackgroundService;
import moe.tristan.easyfxml.model.exception.ExceptionPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static io.vavr.API.unchecked;

/**
 * Created by tristan on 04/03/2017.
 */
@Slf4j
public class SystemIntegration {
    
    private final BackgroundService backgroundService;
    
    public SystemIntegration(final BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }
    
    public void openBrowser(final URL url) {
        if (!Desktop.isDesktopSupported()) {
            SystemIntegration.couldNotOpenDefaultBrowser(url);
            return;
        }

        final URI uri = unchecked(URL::toURI).apply(url);
        this.backgroundService.run(() -> {
            try {
                log.info("Requested opening browser at address : {}", url.toString());
                Desktop.getDesktop().browse(uri);
            } catch (final IOException e) {
                SystemIntegration.couldNotOpenDefaultBrowser(url, e);
            }
        });
    }
    
    private static void couldNotOpenDefaultBrowser(final URL url, final IOException... exception) {
        log.info("Could not get a handle to the OS's browser!");
        ExceptionPane.displayExceptionPane(
                "3rd-party Application Error",
                "We could not open your browser for authentication",
                exception.length != 0 ?
                        exception[0] :
                        new IOException("Could not open browser for page : "+ url.toString())
        );
    }
}
