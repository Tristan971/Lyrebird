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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;

import static moe.lyrebird.model.notifications.NotificationSystemType.INTERNAL;
import static moe.lyrebird.model.notifications.NotificationSystemType.SYSTEM;

@Component
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final ApplicationContext context;
    private final StageManager stageManager;

    @Autowired
    public NotificationService(
            final ApplicationContext context,
            final StageManager stageManager
    ) {
        this.context = context;
        this.stageManager = stageManager;
    }

    public void sendNotification(final Notification notification) {
        LOG.debug("Requesting display of notification with smart display system type.");
        final Stage mainStage = stageManager.getSingle(Screens.ROOT_VIEW).getOrElseThrow(
                () -> new IllegalStateException("Can not find main stage.")
        );

        final boolean isVisible = mainStage.isShowing() && !mainStage.isIconified();

        final NotificationSystemType appropriateNotificationSystem = isVisible ? INTERNAL : SYSTEM;
        LOG.debug("Determined that the appropriate notification system is {}", appropriateNotificationSystem);
        sendNotification(notification, appropriateNotificationSystem);
    }

    public void sendNotification(final Notification notification, final NotificationSystemType notificationSystemType) {
        LOG.debug("Requesting display of notification {} with type {}", notification, notificationSystemType);
        context.getBean(notificationSystemType.getNotificationSystemClass())
               .displayNotification(notification);
    }

}
