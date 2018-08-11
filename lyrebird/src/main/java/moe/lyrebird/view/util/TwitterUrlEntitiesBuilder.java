package moe.lyrebird.view.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.text.Text;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import twitter4a.Status;
import twitter4a.URLEntity;

@Component
public class TwitterUrlEntitiesBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterUrlEntitiesBuilder.class);

    private static final int MAX_TOKEN_CACHE = 1000;

    private final BrowserSupport browserSupport;

    private final Map<Status, List<Token>> tokenizations = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Status, List<Token>> eldest) {
            return size() > MAX_TOKEN_CACHE;
        }
    };

    @Autowired
    public TwitterUrlEntitiesBuilder(BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    public List<Text> asTextFlowTokens(final Status status) {
        List<Token> tokenizationResult = tokenizations.computeIfAbsent(status, this::tokenizeTweetContent);
        return tokenizationResult.stream().map(Token::asTextElement).collect(Collectors.toList());
    }

    private List<Token> tokenizeTweetContent(final Status status) {
        final String input = status.getText();
        final URLEntity[] entities = status.getURLEntities();

        if (entities.length == 0) {
            return processNonUrlEntities(input, entities);
        }

        LOGGER.debug("Tokenizing tweet : {} [{} URL entities]", status.getId(), entities.length);

        final List<Token> nodes = new ArrayList<>(entities.length * 2);

        final List<URLEntity> sortedEntities = Arrays.stream(entities)
                                                     .sorted(Comparator.comparingInt(URLEntity::getStart))
                                                     .collect(Collectors.toList());

        int cursorLeft = 0;
        int cursorRight = 0;
        for (final URLEntity entity : sortedEntities) {
            cursorRight = entity.getStart();
            if (cursorRight > cursorLeft) {
                nodes.add(new Token(input.substring(cursorLeft, cursorRight)));
            }
            cursorLeft = cursorRight;
            cursorRight = entity.getEnd();
            nodes.add(linkOfEntity(entity));
        }

        if (cursorRight < input.length() - 1) {
            nodes.addAll(processNonUrlEntities(input.substring(cursorRight), entities));
        }

        LOGGER.debug("Tokenized tweet as {}", tokenizationResult(nodes));

        return nodes;
    }

    private Token linkOfEntity(final URLEntity urlEntity) {
        return new Token(
                urlEntity.getDisplayURL(),
                TokenType.URL,
                () -> browserSupport.openUrl(urlEntity.getExpandedURL())
        );
    }

    private List<Token> processNonUrlEntities(final String endOfTweet, final URLEntity[] processedEntities) {
        LOGGER.debug("Processing post-entity : {}", endOfTweet);

        String fixedEnd = endOfTweet;
        for (URLEntity processedEntity : processedEntities) {
            fixedEnd = fixedEnd.replaceAll(processedEntity.getURL(), "");
        }

        List<Token> postEntitiesNodes = HyperlinkUtils.findAllUrls(fixedEnd).stream().map(
                url -> new Token(
                        url,
                        TokenType.URL,
                        () -> browserSupport.openUrl(url)
                )
        ).collect(Collectors.toList());
        final Token postText = new Token(HyperlinkUtils.stripAllUrls(fixedEnd));
        postEntitiesNodes.add(0, postText);
        return postEntitiesNodes;
    }

    private List<String> tokenizationResult(final List<Token> nodes) {
        return nodes.stream().map(Token::getTextValue).collect(Collectors.toList());
    }

    private static final class Token {

        private final String textValue;
        private final TokenType tokenType;
        private final Runnable onClick;

        private Token(String textValue, TokenType tokenType, Runnable onClick) {
            this.textValue = textValue;
            this.tokenType = tokenType;
            this.onClick = onClick;
        }

        private Token(String textValue) {
            this(textValue, TokenType.TEXT, null);
        }

        public String getTextValue() {
            return textValue;
        }

        public Text asTextElement() {
            switch (tokenType) {
                case TEXT:
                    return new Text(textValue);
                case URL:
                    return new ClickableHyperlink(textValue, onClick);
            }
            throw new UnsupportedOperationException("Tried to FXify undefined token type! [" + tokenType + "]");
        }

    }

    private enum TokenType {
        TEXT, URL
    }

}
