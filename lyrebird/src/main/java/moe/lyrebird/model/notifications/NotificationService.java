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

import static moe.lyrebird.model.notifications.NotificationSystemType.INTERNAL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;

import moe.lyrebird.model.notifications.system.NotificationSystem;
import moe.lyrebird.view.screens.root.RootScreenComponent;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;

/**
 * This service is in charge of dispatching notification requests to the appropriate {@link NotificationSystem}.
 */
@Component
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final ApplicationContext context;
    private final StageManager stageManager;
    private final RootScreenComponent rootScreenComponent;

    @Autowired
    public NotificationService(ApplicationContext context, StageManager stageManager, RootScreenComponent rootScreenComponent) {
        this.context = context;
        this.stageManager = stageManager;
        this.rootScreenComponent = rootScreenComponent;
    }

    /**
     * Sends a notification to the user, automatically choosing the appropriate {@link NotificationSystem} depending on the current focus state of the main
     * window.
     * <p>
     * Will use the system-level system if the application's main window is not visible (that is, if it is minimized or hidden).
     *
     * @param notification The notification to display.
     */
    public void sendNotification(final Notification notification) {
        LOG.debug("Requesting display of notification with smart display system type.");
        final Stage mainStage = stageManager.getSingle(rootScreenComponent).getOrElseThrow(
                () -> new IllegalStateException("Can not find main stage.")
        );

        final boolean isVisible = mainStage.isShowing() && !mainStage.isIconified();

        // TODO: re-enable native notifications once SystemTray issues are fixed
        final NotificationSystemType appropriateNotificationSystem = INTERNAL;
        LOG.debug("Determined that the appropriate notification system is {}", appropriateNotificationSystem);
        sendNotification(notification, appropriateNotificationSystem);
    }

    /**
     * Sends a notification to the user with a given {@link NotificationSystem}.
     *
     * @param notification           The notification to send
     * @param notificationSystemType The notification system to use for that
     */
    public void sendNotification(final Notification notification, final NotificationSystemType notificationSystemType) {
        LOG.debug("Requesting display of notification {} with type {}", notification, notificationSystemType);
        context.getBean(notificationSystemType.getNotificationSystemClass())
               .displayNotification(notification);
    }

}
