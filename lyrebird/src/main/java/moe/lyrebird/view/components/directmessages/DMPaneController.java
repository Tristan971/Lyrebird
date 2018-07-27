package moe.lyrebird.view.components.directmessages;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.model.components.listview.ComponentCellFxmlController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.DirectMessageEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DMPaneController implements ComponentCellFxmlController<DirectMessageEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DMPaneController.class);

    @FXML
    private Label messageContent;

    @Override
    public void initialize() {

    }

    @Override
    public void updateWithValue(DirectMessageEvent newValue) {
        if (newValue != null) {
            LOG.debug("Direct message pane assigned with displaying message : {}", newValue);
            messageContent.setText(newValue.getText());
        } else {
            messageContent.setText("");
        }
    }

}
