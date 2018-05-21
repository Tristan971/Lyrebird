package moe.lyrebird.view.components.cells;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.components.Components;
import moe.lyrebird.view.components.tweet.TweetPaneController;
import twitter4j.Status;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

@Slf4j
@Component
@RequiredArgsConstructor
public class TweetListCell extends ListCell<Status> {

    private final EasyFxml easyFxml;

    @Override
    protected void updateItem(final Status item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            log.debug("Crafting a tweet pane for status {}", item.getId());
            final FxmlLoadResult<Pane, TweetPaneController> tweetPaneLoad = easyFxml.loadNode(
                    Components.TWEET,
                    Pane.class,
                    TweetPaneController.class
            );

            final Pane tweetPane = tweetPaneLoad.getNode().get();
            final TweetPaneController tpc = tweetPaneLoad.getController().get();
            log.debug(
                    "Made tweet pane for status {} [pane : {}, controller : {}]",
                    item.getId(),
                    tweetPane,
                    tpc
            );

            tpc.setStatus(item);
            setGraphic(tweetPane);
        }
    }

}
