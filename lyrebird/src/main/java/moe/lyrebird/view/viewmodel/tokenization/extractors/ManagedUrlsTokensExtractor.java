package moe.lyrebird.view.viewmodel.tokenization.extractors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.view.viewmodel.tokenization.Token;
import moe.lyrebird.view.viewmodel.tokenization.TokensExtractor;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;

import twitter4j.Status;
import twitter4j.URLEntity;

@Component
public class ManagedUrlsTokensExtractor implements TokensExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedUrlsTokensExtractor.class);

    private final BrowserSupport browserSupport;

    @Autowired
    public ManagedUrlsTokensExtractor(final BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    /**
     * Tokenizes a Tweet based off of {@link Status#getURLEntities()}.
     *
     * @param status The status to tokenize
     *
     * @return A list of {@link Token}s.
     */
    @Override
    public List<Token> extractTokens(final Status status) {
        return Arrays.stream(status.getURLEntities()).map(this::linkOfEntity).collect(Collectors.toList());
    }

    /**
     * Helper method to generate a URL-specialized {@link Token}.
     *
     * @param urlEntity The entity to map as a Token
     *
     * @return A token that correctly this entity.
     */
    private Token linkOfEntity(final URLEntity urlEntity) {
        LOGGER.trace("Tokenizing URLEntity {}", urlEntity);
        return new Token(
                urlEntity.getDisplayURL(),
                urlEntity.getStart(),
                urlEntity.getEnd(),
                Token.TokenType.CLICKABLE,
                () -> browserSupport.openUrl(urlEntity.getExpandedURL())
        );
    }

}
