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

package moe.lyrebird.view.components.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.model.notifications.Notification;
import moe.lyrebird.model.notifications.system.InternalNotificationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

@Component
public class NotificationsController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationsController.class);

    private final InternalNotificationSystem internalNotificationSystem;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private Label notificationTitle;

    @FXML
    private Label notificationContent;

    private final BooleanProperty shouldDisplay = new SimpleBooleanProperty(false);

    @Autowired
    public NotificationsController(final InternalNotificationSystem internalNotificationSystem) {
        this.internalNotificationSystem = internalNotificationSystem;
    }

    @Override
    public void initialize() {
        notificationPane.visibleProperty().bind(shouldDisplay);
        notificationPane.managedProperty().bind(shouldDisplay);
        notificationPane.setOnMouseClicked(e -> shouldDisplay.setValue(false));
        notificationContent.textProperty().addListener((observable, oldValue, newValue) -> {
            notificationContent.visibleProperty().setValue(!newValue.isEmpty());
            notificationContent.managedProperty().setValue(!newValue.isEmpty());
        });
        LOG.debug("Starting internal notification system. Listening on new notification requests.");
        internalNotificationSystem.notificationProperty().addListener((o, prev, cur) -> this.handleChange(cur));
    }

    private void handleChange(final Notification notification) {
        LOG.debug("New notification to display!");
        shouldDisplay.setValue(true);
        LOG.debug("Displaying notification {} internally.", notification);
        Platform.runLater(() -> {
            notificationTitle.setText(notification.getTitle());
            notificationContent.setText(notification.getText());
        });
    }

}
