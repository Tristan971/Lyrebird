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

package moe.lyrebird.model.notifications;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.systemtray.SystemTrayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;

import java.awt.TrayIcon;

@Component
public class AwtNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(AwtNotificationService.class);

    private final Property<TrayIcon> lyrebirdTrayIcon;

    public AwtNotificationService(SystemTrayService trayService) {
        this.lyrebirdTrayIcon = trayService.lyrebirdTrayIconProperty();
    }

    void displayNotification(final Notification notification) {
        LOG.debug("Sending AWT native notification : {}", notification);
        lyrebirdTrayIcon.getValue().displayMessage(
                notification.getTitle(),
                notification.getText(),
                TrayIcon.MessageType.NONE
        );
    }

}
