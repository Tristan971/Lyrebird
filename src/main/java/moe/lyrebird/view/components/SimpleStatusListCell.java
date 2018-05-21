package moe.lyrebird.view.components;

import twitter4j.Status;

import javafx.scene.control.ListCell;

public class SimpleStatusListCell extends ListCell<Status> {

    @Override
    protected void updateItem(final Status item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(null);
        } else {
            setText(item.getUser().getName() + " : " + item.getText());
        }
        setGraphic(null);
    }

}
