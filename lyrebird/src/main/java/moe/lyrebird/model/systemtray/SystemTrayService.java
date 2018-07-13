/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.systemtray;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Component
public class SystemTrayService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTrayService.class);

    private final StageManager stageManager;

    private final Property<TrayIcon> lyrebirdTrayIcon = new SimpleObjectProperty<>(null);

    public SystemTrayService(StageManager stageManager) {
        this.stageManager = stageManager;
        CompletableFuture.supplyAsync(this::loadTrayImage, SwingUtilities::invokeLater)
                         .thenApplyAsync(this::buildTrayIcon, SwingUtilities::invokeLater)
                         .thenAcceptAsync(this::registerTrayIcon, SwingUtilities::invokeLater);
    }

    public Property<TrayIcon> lyrebirdTrayIconProperty() {
        return lyrebirdTrayIcon;
    }

    private Image loadTrayImage() {
        final URL logoUrl = getClass().getClassLoader().getResource("assets/img/logo.png");
        return Toolkit.getDefaultToolkit().getImage(logoUrl);
    }

    private TrayIcon buildTrayIcon(final Image image) {
        return new TrayIcon(image, "Lyrebird", null);
    }

    private void registerTrayIcon(final TrayIcon trayIcon) {
        trayIcon.addActionListener(e -> openMainScreen());
        trayIcon.setImageAutoSize(true);
        try {
            SystemTray.getSystemTray().add(trayIcon);
            lyrebirdTrayIcon.setValue(trayIcon);
        } catch (AWTException e) {
            LOG.error("Could not register tray icon!", e);
        }
    }

    private void openMainScreen() {
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
