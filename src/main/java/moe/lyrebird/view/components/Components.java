package moe.lyrebird.view.components;

import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.lyrebird.view.components.controlbar.ControlBarController;
import moe.lyrebird.view.components.currentaccount.CurrentAccountController;
import moe.lyrebird.view.components.directmessages.DMConversationController;
import moe.lyrebird.view.components.directmessages.DirectMessagesController;
import moe.lyrebird.view.components.mentions.MentionsController;
import moe.lyrebird.view.components.timeline.TimelineController;
import moe.lyrebird.view.components.tweet.TweetPaneController;

public enum Components implements FxmlNode {
    CONTROL_BAR("controlbar/ControlBar.fxml", ControlBarController.class),

    CURRENT_ACCOUNT("currentaccount/CurrentAccount.fxml", CurrentAccountController.class),

    TIMELINE("timeline/Timeline.fxml", TimelineController.class),
    MENTIONS("mentions/Mentions.fxml", MentionsController.class),

    DIRECT_MESSAGES("directmessages/DirectMessages.fxml", DirectMessagesController.class),
    DIRECT_MESSAGE_CONVERSATION("directmessages/DMConversation.fxml", DMConversationController.class),

    TWEET("tweet/TweetPane.fxml", TweetPaneController.class);

    private static final String COMPONENTS_BASE_PATH = "moe/lyrebird/view/components/";

    private final String filePath;
    private final Class<? extends FxmlController> controllerClass;

    Components(final String filePath, final Class<? extends FxmlController> controllerClass) {
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
