package moe.lyrebird.view.viewmodel.tokenization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.text.Text;

import moe.lyrebird.view.viewmodel.javafx.ClickableText;
import moe.lyrebird.model.util.URLMatcher;
import moe.lyrebird.view.viewmodel.tokenization.tokenizers.ManagedUrlsTokensExtractor;

import twitter4a.Status;

/**
 * This class helps with tokenization of Tweet content to make sure elements expected to be clickable are not
 * rendered as simple {@link Text} but really as proper links (i.e. {@link ClickableText}), that is to say
 * {@link Text} elements (because {@link javafx.scene.control.Hyperlink} does not wrap properly) that will open
 * a browser on click.
 *
 * @see Status#getURLEntities()
 * @see URLMatcher
 * @see Token
 */
@Component
public class TweetContentTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetContentTokenizer.class);

    private static final int MAX_TOKEN_CACHE = 1000;

    private final Map<Status, List<Token>> tokenizations = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Status, List<Token>> eldest) {
            return size() > MAX_TOKEN_CACHE;
        }
    };

    private final ManagedUrlsTokensExtractor managedUrlsTokensExtractor;

    @Autowired
    public TweetContentTokenizer(final ManagedUrlsTokensExtractor managedUrlsTokensExtractor) {
        this.managedUrlsTokensExtractor = managedUrlsTokensExtractor;
    }

    /**
     * Calls {@link ManagedUrlsTokensExtractor#extractTokens(Status)} via the {@link #tokenizations} local cache to not
     * recompute tokens for a tweet on scrolling/replying/consulting user details etc.
     *
     * @param status The status to tokenize
     *
     * @return The list of {@link Text} elements that will be put into the {@link javafx.scene.text.TextFlow} that
     * represents the content of a Tweet.
     */
    public List<Text> asTextFlowTokens(final Status status) {
        final List<Token> tokenizationResult = tokenizations.computeIfAbsent(status, aStatus -> {
            final List<Token> tokenizedTweet = managedUrlsTokensExtractor.extractTokens(aStatus);
            LOGGER.debug("Tokenized status {} as : {}", status.getId(), tokenizationResult(tokenizedTweet));
            return tokenizedTweet;
        });
        return tokenizationResult.stream().map(Token::asTextElement).collect(Collectors.toList());
    }

    /**
     * Simple helper for debugging. Prints a list of token as a String-convertible representation.
     *
     * @param tokens The tokens that will be printed.
     *
     * @return The string-convertible list of the tokens given as parameter.
     */
    private static List<String> tokenizationResult(final List<Token> tokens) {
        return tokens.stream().map(Token::getOriginalStringValue).collect(Collectors.toList());
    }

}
