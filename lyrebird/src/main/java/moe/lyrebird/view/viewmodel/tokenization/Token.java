package moe.lyrebird.view.viewmodel.tokenization;

import javafx.scene.text.Text;

import moe.lyrebird.view.viewmodel.javafx.ClickableText;

/**
 * This class represents a String-convertible token that can either represent some {@link TokenType#SIMPLE_TEXT} or
 * a {@link TokenType#CLICKABLE} element.
 */
public final class Token {

    private static final Runnable NO_OP = () -> {
    };

    private final String originalStringValue;
    private final String replacedStringValue;
    private final TokenPosition tokenPosition;
    private final TokenType tokenType;
    private final Runnable onClick;

    public Token(final String originalStringValue, final String replacedStringValue, final TokenPosition tokenPosition, final TokenType tokenType, final Runnable onClick) {
        this.originalStringValue = originalStringValue;
        this.replacedStringValue = replacedStringValue;
        this.tokenPosition = tokenPosition;
        this.tokenType = tokenType;
        this.onClick = onClick;
    }

    public static Token simpleText(final String text, final int begin, final int end) {
        return new Token(
                text,
                text,
                new TokenPosition(begin, end),
                TokenType.SIMPLE_TEXT,
                NO_OP
        );
    }

    public String getOriginalStringValue() {
        return originalStringValue;
    }

    public String getReplacedStringValue() {
        return replacedStringValue;
    }

    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public static Text asTextElement(final Token token) {
        if (TokenType.SIMPLE_TEXT.equals(token.getTokenType())) {
            return new Text(token.getReplacedStringValue());
        } else {
            return new ClickableText(token.getReplacedStringValue(), token.onClick);
        }
    }

    /**
     * Simple enum flag for whether a given {@link Token} is actually plain text or a URL.
     */
    public enum TokenType {
        SIMPLE_TEXT,
        CLICKABLE
    }

    public static class TokenPosition {

        private final int begin;
        private final int end;

        public TokenPosition(final int begin, final int end) {
            this.begin = begin;
            this.end = end;
        }

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }

    }

}

