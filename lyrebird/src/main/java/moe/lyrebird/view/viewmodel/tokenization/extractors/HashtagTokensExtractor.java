package moe.lyrebird.view.viewmodel.tokenization.extractors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.view.viewmodel.tokenization.Token;
import moe.lyrebird.view.viewmodel.tokenization.TokensExtractor;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;

import twitter4a.Status;

@Component
public class HashtagTokensExtractor implements TokensExtractor {

    private static final String HASHTAG_SEARCH_BASE_URL = "https://twitter.com/hashtag/";

    private final BrowserSupport browserSupport;

    @Autowired
    public HashtagTokensExtractor(final BrowserSupport browserSupport) {
        this.browserSupport = browserSupport;
    }

    @Override
    public List<Token> extractTokens(final Status status) {
        return Arrays.stream(status.getHashtagEntities()).map(hashtag -> new Token(
                "#" + hashtag.getText(),
                hashtag.getStart(),
                hashtag.getEnd(),
                () -> browserSupport.openUrl(HASHTAG_SEARCH_BASE_URL + hashtag.getText())
        )).collect(Collectors.toList());
    }

}
