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

import java.awt.TrayIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.OS;

/**
 * This class is responsible for management and exposure of the {@link LyrebirdTrayIcon}.
 */
@Component
public class SystemTrayService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTrayService.class);

    static {
        if (OS.isLinux()) {
            SystemTray.FORCE_TRAY_TYPE = SystemTray.TrayType.GtkStatusIcon;
        }
    }

    private final LyrebirdTrayIcon lyrebirdTrayIcon;

    private final Property<TrayIcon> trayIcon = new SimpleObjectProperty<>(null);

    public SystemTrayService(final LyrebirdTrayIcon lyrebirdTrayIcon) {
        this.lyrebirdTrayIcon = lyrebirdTrayIcon;
        loadTrayIcon();
    }

    public Property<TrayIcon> trayIconProperty() {
        return trayIcon;
    }

    /**
     * Loads the {@link LyrebirdTrayIcon} in the current OS's system tray.
     */
    private void loadTrayIcon() {
        LOG.debug("Registering tray icon for Lyrebird...");

        LOG.debug("Creating a tray icon...");
        SystemTray tray = SystemTray.get();
        tray.setImage(lyrebirdTrayIcon.getIcon());
        tray.setTooltip("Lyrebird");

        LOG.debug("Adding items to tray icon's menu...");
        final Menu menu = tray.getMenu();

        lyrebirdTrayIcon.getMenuItems().forEach((item, action) -> {
            LOG.debug("Adding menu item : {} (callback : {})", item, action);
            final MenuItem menuItem = new MenuItem(item.getLabel());
            menuItem.setCallback(action);
            menu.add(menuItem);
        });

        LOG.debug("Finished creating tray icon!");
    }

}
