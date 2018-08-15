package moe.lyrebird.model.systemtray;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import moe.lyrebird.view.screens.Screen;
import moe.tristan.easyfxml.model.awt.integrations.SystemTrayIcon;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;

/**
 * The {@link SystemTrayIcon} that is registered in the current OS's tray bar.
 */
@Component
public class LyrebirdTrayIcon implements SystemTrayIcon {

    private static final Logger LOG = LoggerFactory.getLogger(LyrebirdTrayIcon.class);

    private final Environment environment;
    private final StageManager stageManager;

    public LyrebirdTrayIcon(
            final Environment environment,
            final StageManager stageManager
    ) {
        this.environment = environment;
        this.stageManager = stageManager;
    }

    @Override
    public String getLabel() {
        return environment.getRequiredProperty("app.promo.name");
    }

    @Override
    @Cacheable("lyrebirdTrayIconURL")
    public URL getIcon() {
        return getClass().getClassLoader().getResource("assets/img/general_icon_lyrebird_logo_small.png");
    }

    @Override
    public Map<MenuItem, ActionListener> getMenuItems() {
        final Map<MenuItem, ActionListener> menuItems = new LinkedHashMap<>();
        menuItems.put(new MenuItem("Open Lyrebird", null), e -> showMainStage());
        menuItems.put(new MenuItem("Quit Lyrebird", null), e -> Platform.runLater(this::exitApplication));
        return menuItems;
    }

    @Override
    public MouseListener onMouseClickListener() {
        //noop
        return null;
    }

    /**
     * Fetches, shows and moves the main application stage to the front.
     */
    private void showMainStage() {
        CompletableFuture.runAsync(
                () -> stageManager.getSingle(Screen.ROOT_VIEW)
                                  .toTry(IllegalStateException::new)
                                  .onSuccess(stage -> {
                                      stage.show();
                                      stage.setIconified(false);
                                  }).onFailure(err -> LOG.error("Could not show main stage!", err)),
                Platform::runLater
        );
    }

    /**
     * Requests closure of the application.
     */
    private void exitApplication() {
        LOG.info("Requesting application closure from tray icon.");
        stageManager.getSingle(Screen.ROOT_VIEW).getOrElseThrow(IllegalStateException::new).close();
        Platform.exit();
    }

}
