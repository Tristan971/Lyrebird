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

/**
 * This class helps with tokenization of Tweet content to make sure elements expected to be clickable are not
 * rendered as simple {@link Text} but really as proper links (i.e. {@link ClickableHyperlink}), that is to say
 * {@link Text} elements (because {@link javafx.scene.control.Hyperlink} does not wrap properly) that will open
 * a browser on click.
 *
 * @see Status#getURLEntities()
 * @see HyperlinkUtils
 * @see Token
 */
@Component
public class TwitterContentTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterContentTokenizer.class);

    private static final int MAX_TOKEN_CACHE = 1000;

    private final BrowserSupport browserSupport;

    private final Map<Status, List<Token>> tokenizations = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Status, List<Token>> eldest) {
            return size() > MAX_TOKEN_CACHE;
        }
    };

    @Autowired
    public TwitterContentTokenizer(BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    /**
     * Calls {@link #tokenizeTweetContent(Status)} via the {@link #tokenizations} local cache to not recompute
     * tokens for a tweet on scrolling/replying/consulting user details etc.
     *
     * @param status The status to tokenize
     *
     * @return The list of {@link Text} elements that will be put into the {@link javafx.scene.text.TextFlow} that
     * represents the content of a Tweet.
     */
    public List<Text> asTextFlowTokens(final Status status) {
        List<Token> tokenizationResult = tokenizations.computeIfAbsent(status, this::tokenizeTweetContent);
        return tokenizationResult.stream().map(Token::asTextElement).collect(Collectors.toList());
    }

    /**
     * Uses a C-style cursor approach to efficiently tokenize a Tweet based off of a first pass prebuilding links
     * via {@link Status#getURLEntities()} and a second pass for other URLs that are not considered by Twitter as
     * {@link URLEntity}s via {@link #processNonUrlEntities(String, URLEntity[])}. (i.e. embedded media links).
     *
     * @param status The status to tokenize
     *
     * @return A list of {@link Token}s.
     */
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

    /**
     * Helper method to generate a URL-specialized {@link Token}.
     *
     * @param urlEntity The entity to map as a Token
     *
     * @return A token that correctly this entity.
     */
    private Token linkOfEntity(final URLEntity urlEntity) {
        return new Token(
                urlEntity.getDisplayURL(),
                TokenType.URL,
                () -> browserSupport.openUrl(urlEntity.getExpandedURL())
        );
    }

    /**
     * This method uses {@link HyperlinkUtils} to process URLs in the tweet content that are not considered
     * by Twitter to be proper {@link URLEntity}s (like embedded media links) but still need to be clickable by
     * the end-user.
     *
     * @param endOfTweet        These non-URL url entities always are at the end of a tweet
     * @param processedEntities The entities already processed to avoid reprinting in case something bad
     *                          happens. Mostly useless right now.
     *
     * @return The list of {@link Token} that cannot be built from {@link Status#getURLEntities()} alone.
     */
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

    /**
     * Simple helper for debugging. Prints a list of token as a String-convertible representation.
     *
     * @param tokens The tokens that will be printed.
     *
     * @return The string-convertible list of the tokens given as parameter.
     */
    private List<String> tokenizationResult(final List<Token> tokens) {
        return tokens.stream().map(Token::getTextValue).collect(Collectors.toList());
    }

    /**
     * This class represents a String-convertible token that can either represent some simple {@link TokenType#TEXT} or
     * a clickable {@link TokenType#URL}.
     */
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

    /**
     * Simple enum flag for whether a given {@link Token} is actually plain text or a URL.
     */
    private enum TokenType {
        TEXT, URL
    }

}
