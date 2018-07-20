package moe.lyrebird.model.systemtray;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.awt.integrations.SystemTrayIcon;
import moe.tristan.easyfxml.model.awt.objects.OnMouseClickListener;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;

import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        return environment.getProperty("app.name");
    }

    @Override
    @Cacheable("lyrebirdTrayIconURL")
    public URL getIcon() {
        return getClass().getClassLoader().getResource("assets/img/logo_small.png");
    }

    @Override
    public Map<MenuItem, ActionListener> getMenuItems() {
        final Map<MenuItem, ActionListener> menuItems = new LinkedHashMap<>();
        menuItems.put(new MenuItem("Open", null), e -> showMainStage());
        return menuItems;
    }

    @Override
    public MouseListener onMouseClickListener() {
        return new OnMouseClickListener(e -> {
            LOG.debug("Registered a click on the tray icon!");
            if (e.getButton() == MouseEvent.BUTTON1) {
                LOG.debug("Requested display of main stage!");
                this.showMainStage();
            }
        });
    }

    private void showMainStage() {
        CompletableFuture.runAsync(
                () -> stageManager.getSingle(Screens.ROOT_VIEW)
                                  .toTry(IllegalStateException::new)
                                  .onSuccess(stage -> {
                                      stage.show();
                                      stage.setIconified(false);
                                      stage.toFront();
                                  }).onFailure(err -> LOG.error("Could not show main stage!", err)),
                Platform::runLater
        );
    }

}
