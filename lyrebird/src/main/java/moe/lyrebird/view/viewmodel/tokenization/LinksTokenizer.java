package moe.lyrebird.view.viewmodel.tokenization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.view.viewmodel.HyperlinkUtils;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;

import twitter4a.Status;
import twitter4a.URLEntity;

@Component
public class LinksTokenizer implements Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinksTokenizer.class);

    private final BrowserSupport browserSupport;

    @Autowired
    public LinksTokenizer(BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    @Override
    public List<Token> tokenize(Status status) {
        return tokenizeTweetContent(status);
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
            cursorRight = entity.getEnd();
            cursorLeft = cursorRight;
            nodes.add(linkOfEntity(entity));
        }

        if (cursorRight < input.length() - 1) {
            nodes.addAll(processNonUrlEntities(input.substring(cursorRight), entities));
        }

        return nodes;
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
                        Token.TokenType.URL,
                        () -> browserSupport.openUrl(url)
                )
        ).collect(Collectors.toList());
        final Token postText = new Token(HyperlinkUtils.stripAllUrls(fixedEnd));
        postEntitiesNodes.add(0, postText);
        return postEntitiesNodes;
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
                Token.TokenType.URL,
                () -> browserSupport.openUrl(urlEntity.getExpandedURL())
        );
    }

}
