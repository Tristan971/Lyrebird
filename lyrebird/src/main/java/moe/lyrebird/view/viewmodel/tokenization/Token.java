package moe.lyrebird.view.viewmodel.tokenization;

import javafx.scene.text.Text;

import moe.lyrebird.view.viewmodel.ClickableHyperlink;

/**
 * This class represents a String-convertible token that can either represent some simple {@link TokenType#TEXT} or
 * a clickable {@link TokenType#URL}.
 */
public final class Token {

    private final String textValue;
    private final TokenType tokenType;
    private final Runnable onClick;

    public Token(String textValue, TokenType tokenType, Runnable onClick) {
        this.textValue = textValue;
        this.tokenType = tokenType;
        this.onClick = onClick;
    }

    public Token(String textValue) {
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

    /**
     * Simple enum flag for whether a given {@link Token} is actually plain text or a URL.
     */
    public enum TokenType {
        TEXT,
        URL,
        MENTION,
        HASHTAG
    }

}

