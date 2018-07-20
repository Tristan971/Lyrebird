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
import moe.tristan.easyfxml.model.awt.integrations.SystemTraySupport;
import moe.lyrebird.model.interrupts.CleanupOperation;
import moe.lyrebird.model.interrupts.CleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.TrayIcon;

@Component
public class SystemTrayService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTrayService.class);

    private final LyrebirdTrayIcon lyrebirdTrayIcon;
    private final SystemTraySupport traySupport;
    private final CleanupService cleanupService;

    private final Property<TrayIcon> trayIcon = new SimpleObjectProperty<>(null);

    public SystemTrayService(
            final LyrebirdTrayIcon lyrebirdTrayIcon,
            final SystemTraySupport traySupport,
            final CleanupService cleanupService
    ) {
        this.lyrebirdTrayIcon = lyrebirdTrayIcon;
        this.traySupport = traySupport;
        this.cleanupService = cleanupService;
        loadTrayIcon();
    }

    public Property<TrayIcon> trayIconProperty() {
        return trayIcon;
    }

    private void loadTrayIcon() {
        LOG.debug("Registering tray icon for Lyrebird...");
        traySupport.registerTrayIcon(lyrebirdTrayIcon)
                   .thenApplyAsync(trayIconRes -> trayIconRes.getOrElse(() -> {
                       LOG.error("Could not load the tray icon!", trayIconRes.getCause());
                       return null;
                   }))
                   .thenAcceptAsync(trayIcon::setValue)
                   .thenRunAsync(() -> {
                       if (trayIcon.getValue() == null) {
                           return;
                       }
                       cleanupService.registerCleanupOperation(new CleanupOperation(
                               "Remove system tray icon.",
                               () -> traySupport.removeTrayIcon(trayIcon.getValue())
                       ));
                   });
    }

}
