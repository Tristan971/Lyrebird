package moe.lyrebird.view.timeline;

import javafx.scene.control.ListCell;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class SimpleTweetListCell extends ListCell<Status> {
    @Override
    protected void updateItem(final Status status, final boolean empty) {
        super.updateItem(status, empty);

        setTextAlignment(TextAlignment.LEFT);
        setText(statusAsString(status));
    }

    private String statusAsString(final Status status) {
        return status.getText();
    }
}
