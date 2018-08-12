package moe.lyrebird.view.viewmodel.tokenization.extractors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.util.URLMatcher;
import moe.lyrebird.view.viewmodel.tokenization.Token;
import moe.lyrebird.view.viewmodel.tokenization.TokensExtractor;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;

import twitter4a.Status;
import twitter4a.URLEntity;

/**
 * This class uses {@link URLMatcher} to process URLs in the tweet content that are not considered
 * by Twitter to be proper {@link URLEntity}s (like embedded media links) but still need to be clickable by
 * the end-user.
 */
@Component
public class UnmanagedUrlsTokensExtractor implements TokensExtractor {

    private final BrowserSupport browserSupport;

    @Autowired
    public UnmanagedUrlsTokensExtractor(final BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    /**
     * @return The list of {@link Token}s that cannot be built from {@link Status#getURLEntities()} alone.
     */
    @Override
    public List<Token> extractTokens(final Status status) {
        final String statusText = status.getText();
        final URLEntity[] managedUrlEntities = status.getURLEntities();

        String unamanagedText = statusText;
        for (final URLEntity processedEntity : managedUrlEntities) {
            unamanagedText = unamanagedText.replaceAll(processedEntity.getURL(), "");
        }

        return URLMatcher.findAllUrlsWithPosition(unamanagedText).stream().map(
                urlWithPos -> new Token(
                        urlWithPos._1,
                        urlWithPos._1,
                        urlWithPos._2,
                        urlWithPos._3,
                        Token.TokenType.CLICKABLE,
                        () -> browserSupport.openUrl(urlWithPos._1)
                )
        ).collect(Collectors.toList());
    }

}
