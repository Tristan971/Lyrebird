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

package moe.lyrebird.model.notifications.system;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.notifications.Notification;
import moe.lyrebird.view.components.notifications.NotificationsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

/**
 * JavaFX-based application-internal implementation of {@link NotificationSystem}.
 */
@Component
public class InternalNotificationSystem implements NotificationSystem {

    private static final Logger LOG = LoggerFactory.getLogger(InternalNotificationSystem.class);

    private final Property<Notification> notificationProperty = new SimpleObjectProperty<>(null);

    /**
     * Sets-up the notification to display property for consumption by {@link NotificationsController}.
     *
     * @param notification the notification to display
     */
    @Override
    public void displayNotification(final Notification notification) {
        LOG.debug("Queuing notification {} for display", notification);
        notificationProperty.setValue(notification);
    }

    /**
     * @return The notification currently requested for displaying
     */
    public Property<Notification> notificationProperty() {
        return notificationProperty;
    }

}
