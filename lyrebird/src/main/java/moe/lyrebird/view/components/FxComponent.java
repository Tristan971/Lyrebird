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

package moe.lyrebird.view.components;

import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.lyrebird.view.components.controlbar.ControlBarController;
import moe.lyrebird.view.components.credits.CreditController;
import moe.lyrebird.view.components.currentaccount.CurrentAccountController;
import moe.lyrebird.view.components.directmessages.DMConversationController;
import moe.lyrebird.view.components.directmessages.DirectMessagePaneController;
import moe.lyrebird.view.components.directmessages.DirectMessagesController;
import moe.lyrebird.view.components.mentions.MentionsController;
import moe.lyrebird.view.components.notifications.NotificationsController;
import moe.lyrebird.view.components.timeline.TimelineController;
import moe.lyrebird.view.components.tweet.TweetInterractionPaneController;
import moe.lyrebird.view.components.tweet.TweetPaneController;
import moe.lyrebird.view.components.usertimeline.UserTimelineController;
import moe.lyrebird.view.screens.Screen;

/**
 * Components are {@link FxmlNode} that do not have their own stage but are embedded inside a {@link Screen} member.
 *
 * @see FxmlNode
 * @see Screen
 */
public enum FxComponent implements FxmlNode {
    CONTROL_BAR("controlbar/ControlBar.fxml", ControlBarController.class),

    CURRENT_ACCOUNT("currentaccount/CurrentAccount.fxml", CurrentAccountController.class),

    TIMELINE("timeline/Timeline.fxml", TimelineController.class),
    MENTIONS("mentions/Mentions.fxml", MentionsController.class),

    DIRECT_MESSAGES("directmessages/DirectMessages.fxml", DirectMessagesController.class),
    DIRECT_MESSAGE_CONVERSATION("directmessages/DMConversation.fxml", DMConversationController.class),
    DIRECT_MESSAGE_PANE("directmessages/DirectMessagePane.fxml", DirectMessagePaneController.class),

    TWEET("tweet/TweetPane.fxml", TweetPaneController.class),
    TWEET_INTERRACTION_BOX("tweet/TweetInterractionPane.fxml", TweetInterractionPaneController.class),

    CREDIT("credits/CreditPane.fxml", CreditController.class),

    USER_TIMELINE("usertimeline/UserTimeline.fxml", UserTimelineController.class),

    NOTIFICATIONS_PANE("notifications/NotificationPane.fxml", NotificationsController.class);

    private static final String COMPONENTS_BASE_PATH = "moe/lyrebird/view/components/";

    private final String filePath;
    private final Class<? extends FxmlController> controllerClass;

    FxComponent(final String filePath, final Class<? extends FxmlController> controllerClass) {
        this.filePath = filePath;
        this.controllerClass = controllerClass;
    }

    @Override
    public FxmlFile getFile() {
        return () -> COMPONENTS_BASE_PATH + filePath;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return controllerClass;
    }
}
