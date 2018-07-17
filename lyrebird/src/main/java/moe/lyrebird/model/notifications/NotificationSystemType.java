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

import moe.lyrebird.model.notifications.system.AwtNotificationSystem;
import moe.lyrebird.model.notifications.system.InternalNotificationSystem;
import moe.lyrebird.model.notifications.system.NotificationSystem;

public enum NotificationSystemType {
    INTERNAL(InternalNotificationSystem.class),
    SYSTEM(AwtNotificationSystem.class);

    private final Class<? extends NotificationSystem> notificationSystemClass;

    NotificationSystemType(final Class<? extends NotificationSystem> notificationSystemClass) {
        this.notificationSystemClass = notificationSystemClass;
    }

    public Class<? extends NotificationSystem> getNotificationSystemClass() {
        return notificationSystemClass;
    }

}
