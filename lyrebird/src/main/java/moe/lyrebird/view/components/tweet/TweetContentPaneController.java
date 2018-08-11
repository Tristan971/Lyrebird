package moe.lyrebird.view.components.tweet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import moe.lyrebird.view.util.TwitterUrlEntitiesBuilder;
import moe.tristan.easyfxml.api.FxmlController;
import twitter4a.Status;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetContentPaneController implements FxmlController {

    @FXML
    private TextFlow tweetContent;

    private final TwitterUrlEntitiesBuilder twitterUrlEntitiesBuilder;

    private final Property<Status> statusProp = new SimpleObjectProperty<>();

    @Autowired
    public TweetContentPaneController(TwitterUrlEntitiesBuilder twitterUrlEntitiesBuilder) {
        this.twitterUrlEntitiesBuilder = twitterUrlEntitiesBuilder;
    }

    @Override
    public void initialize() {
        if (statusProp.getValue() != null) {
            statusReady();
        }
        statusProp.addListener((observable, oldValue, newValue) -> statusReady());
        VBox.setVgrow(tweetContent, Priority.ALWAYS);
    }


    public void setStatusProp(final Status status) {
        this.statusProp.setValue(status);
    }

    private void statusReady() {
        final List<Text> textFlowElements = twitterUrlEntitiesBuilder.asTextFlowTokens(statusProp.getValue());
        tweetContent.getChildren().setAll(textFlowElements);
    }

}
