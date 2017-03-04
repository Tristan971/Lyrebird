package moe.lyrebird.system;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.views.ErrorPane;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by tristan on 04/03/2017.
 */
@Slf4j
@UtilityClass
public class DefaultApplications {
    public static void openBrowser(final URL url) {
        log.info("Requested opening browser at address : {}", url.toString());
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().browse(url.toURI());
        } catch (final URISyntaxException e) {
            log.info("Bad URL! [{}]", url.toString());
            ErrorPane.displayErrorPaneOf("Bad URL!" + url.toString(), e);
        } catch (final IOException e) {
            log.info("Could not get a handle to the OS's browser!");
            ErrorPane.displayErrorPaneOf(
                    "Browser unavailable!\n" +
                            "We couldn't open your default browser, so you need" +
                            "to access the following URL " + url.toString() +
                            "manually with your preferred browser.",
                    e
            );
        }
    }
}
