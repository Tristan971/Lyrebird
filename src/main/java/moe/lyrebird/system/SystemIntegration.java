package moe.lyrebird.system;

import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.SneakyThrow;
import moe.lyrebird.model.threading.BackgroundService;
import moe.lyrebird.view.views.ErrorPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

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
        log.info("Requested opening browser at address : {}", url.toString());
        if (!Desktop.isDesktopSupported()) {
            SystemIntegration.couldNotOpenDefaultBrowser(url);
            return;
        }

        final URI uri = SneakyThrow.unchecked(url::toURI);
        this.backgroundService.run(() -> {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (final IOException e) {
                SystemIntegration.couldNotOpenDefaultBrowser(url, e);
            }
        });
    }
    
    private static void couldNotOpenDefaultBrowser(final URL url, final IOException... exception) {
        log.info("Could not get a handle to the OS's browser!");
        ErrorPane.displayErrorPaneOf(
                "Browser unavailable!\n" +
                        "We couldn't open your default browser, so you need" +
                        "to access the following URL " + url.toString() +
                        "manually with your preferred browser.",
                exception.length != 0 ? exception[0] : new IOException("No exception was thrown")
        );
    }
}
