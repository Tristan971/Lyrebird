package moe.lyrebird.system;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.SneakyThrow;
import moe.lyrebird.model.threading.ThreadUtils;
import moe.lyrebird.view.views.ErrorPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Created by tristan on 04/03/2017.
 */
@Slf4j
@UtilityClass
public class DefaultApplications {

    public static void openBrowser(final URL url) {
        log.info("Requested opening browser at address : {}", url.toString());
        if (!Desktop.isDesktopSupported()) {
            couldNotOpenDefaultBrowser(url);
            return;
        }

        final URI uri = SneakyThrow.unchecked(url::toURI);
        ThreadUtils.run(() -> {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (final IOException e) {
                couldNotOpenDefaultBrowser(url, e);
            }
        });
    }

    private static void couldNotOpenDefaultBrowser(final URL url, final IOException ... exception) {
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
