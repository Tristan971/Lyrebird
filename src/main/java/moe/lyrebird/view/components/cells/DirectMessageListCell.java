package moe.lyrebird.view.components.cells;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import twitter4j.DirectMessage;

import javafx.scene.control.ListCell;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class DirectMessageListCell extends ListCell<DirectMessage> {

    @Override
    protected void updateItem(final DirectMessage item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
        } else {
            setText(item.getText());
        }
    }

}
