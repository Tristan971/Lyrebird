package moe.lyrebird.view.components.tweet;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.lyrebird.view.util.HyperlinkUtils;
import twitter4a.Status;
import twitter4a.URLEntity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetContentPaneController implements FxmlController {

    @FXML
    private TextFlow tweetContent;

    private final Property<Status> statusProp = new SimpleObjectProperty<>();

    @Override
    public void initialize() {
        if (statusProp.getValue() != null) {
            statusReady();
        }
        statusProp.addListener((observable, oldValue, newValue) -> statusReady());
    }


    public void setStatusProp(final Status statusProp) {
        this.statusProp.setValue(statusProp);
    }

    private void statusReady() {
        tweetContent.getChildren().clear();
        final String expanded = HyperlinkUtils.transformUrls(
                statusProp.getValue().getText(),
                this::expandedUrl
        );

        tweetContent.getChildren().add(new Text(expanded));
    }

    private String expandedUrl(final String shortUrl) {
        return Arrays.stream(statusProp.getValue().getURLEntities())
                     .filter(entity -> entity.getURL().equals(shortUrl))
                     .map(URLEntity::getExpandedURL)
                     .findAny()
                     .orElse(shortUrl);
    }
}
