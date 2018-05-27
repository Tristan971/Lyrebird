package moe.lyrebird.view.components.cells;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.components.Components;
import moe.lyrebird.view.components.tweet.TweetPaneController;
import twitter4j.Status;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetListCell extends ListCell<Status> {

    private final Pane tweetPane;
    private final TweetPaneController tweetPaneController;

    public TweetListCell(final EasyFxml easyFxml) {
        super();

        final FxmlLoadResult<Pane, TweetPaneController> loadResult = easyFxml.loadNode(
                Components.TWEET,
                Pane.class,
                TweetPaneController.class
        );
        this.tweetPane = loadResult.getNode().get();
        this.tweetPaneController = loadResult.getController().get();
        log.trace("Created a Tweet cell with pane {} and controller {}", tweetPane, tweetPaneController);
    }

    @Override
    protected void updateItem(final Status item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
        } else {
            log.trace("Filling TweetPane[{}] with status {}", tweetPane, item.getId());
            Platform.runLater(() -> {
                tweetPaneController.setStatus(item);
                tweetPaneController.selected.bind(selectedProperty());
                if (getGraphic() == null) {
                    setGraphic(tweetPane);
                }
            });
        }
    }

}
