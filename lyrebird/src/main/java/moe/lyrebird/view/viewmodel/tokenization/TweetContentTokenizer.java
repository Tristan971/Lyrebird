package moe.lyrebird.view.viewmodel.tokenization;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.text.Text;

import moe.lyrebird.model.util.URLMatcher;
import moe.lyrebird.view.viewmodel.javafx.ClickableText;

import twitter4j.Status;

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

    private static final int MAX_TOKEN_CACHE = 1024;

    private final Map<Status, List<Token>> tokenizations = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Status, List<Token>> eldest) {
            return size() > MAX_TOKEN_CACHE;
        }
    };

    private final List<TokensExtractor> tokensExtractors;

    @Autowired
    public TweetContentTokenizer(final List<TokensExtractor> tokensExtractors) {
        this.tokensExtractors = tokensExtractors;
    }

    /**
     * Computes the appropriate {@link Text} elements to represent the given {@link Status}' text content in a {@link
     * javafx.scene.text.TextFlow}.
     *
     * @param status The {@link Status}' whose text will be presented in a {@link javafx.scene.text.TextFlow}.
     *
     * @return The {@link List} of {@link Text} elements that will be put into the {@link javafx.scene.text.TextFlow} that
     * represents the content of a Tweet.
     */
    public List<Text> asTextFlowTokens(final Status status) {
        return tokenizations.computeIfAbsent(status, this::tokenize)
                            .stream()
                            .map(Token::asTextElement)
                            .collect(Collectors.toList());
    }

    /**
     * Tokenizes a given {@link Status} as a {@link List} of {@link Token}s.
     * <p>
     * Calls {@link TokensExtractor#extractTokens(Status)} on all the supported {@link TokensExtractor}s to compute
     * the special {@link Token}s and collects the "leftover" text elements via {@link
     * SimpleTextTokensCollector#collectLeftovers(Status, List)}.
     * <p>
     * Caches the tokenization results via {@link Map#computeIfAbsent(Object, Function)} on {@link #tokenizations}.
     *
     * @param status The status to tokenize.
     *
     * @return The {@link Token}ized representation of the given {@link Status}.
     */
    private List<Token> tokenize(final Status status) {
        LOGGER.trace("Tokenizing status {}", status.getId());
        final List<Token> clickableTokens = tokensExtractors.stream()
                                                            .map(extractor -> extractor.extractTokens(status))
                                                            .flatMap(List::stream)
                                                            .collect(Collectors.toList());

        final List<Token> simpleTextTokens = SimpleTextTokensCollector.collectLeftovers(status, clickableTokens);

        final List<Token> tokenization = Stream.of(clickableTokens, simpleTextTokens)
                                               .flatMap(List::stream)
                                               .sorted(Comparator.comparingInt(Token::getBegin))
                                               .collect(Collectors.toList());

        LOGGER.trace("Tokenized status {} as : {}", status.getId(), tokenizationStringValue(tokenization));
        return tokenization;
    }

    /**
     * Simple helper for debugging. Prints a list of token as a String-convertible representation.
     *
     * @param tokens The tokens that will be printed.
     *
     * @return The string-convertible list of the tokens given as parameter.
     */
    private static List<String> tokenizationStringValue(final List<Token> tokens) {
        return tokens.stream().map(Token::getTextRepresentation).collect(Collectors.toList());
    }

}
