package moe.lyrebird.view.components.tweet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.lyrebird.view.util.ClickableHyperlink;
import moe.lyrebird.view.util.HyperlinkUtils;
import twitter4a.Status;
import twitter4a.URLEntity;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TweetContentPaneController implements FxmlController {

    @FXML
    private TextFlow tweetContent;

    private final BrowserSupport browserSupport;

    private final Property<Status> statusProp = new SimpleObjectProperty<>();

    @Autowired
    public TweetContentPaneController(final BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

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
        final List<Node> textNodes = clickableUrls(expanded);
        textNodes.forEach(node -> {
            tweetContent.getChildren().add(node);
            tweetContent.getChildren().add(new Text(" "));
        });
    }

    private String expandedUrl(final String shortUrl) {
        return Arrays.stream(statusProp.getValue().getURLEntities())
                     .filter(entity -> entity.getURL().equals(shortUrl))
                     .map(URLEntity::getExpandedURL)
                     .findAny()
                     .orElse(shortUrl);
    }

    private List<Node> clickableUrls(final String content) {
        return Arrays.stream(content.split(" "))
                     .map(element -> {
                         if (HyperlinkUtils.isUrl(element)) {
                             return new ClickableHyperlink(element, browserSupport::openUrl);
                         } else {
                             return new Text(element);
                         }
                     }).collect(Collectors.toList());
    }
}
