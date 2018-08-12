package moe.lyrebird.view.viewmodel.tokenization;

import javafx.scene.text.Text;

import moe.lyrebird.view.viewmodel.javafx.ClickableText;

/**
 * This class represents a String-convertible token that can either represent some {@link TokenType#SIMPLE_TEXT} or
 * a {@link TokenType#CLICKABLE} element.
 */
public final class Token {

    private final String textRepresentation;
    private final int begin;
    private final int end;
    private final TokenType tokenType;
    private final Runnable onClick;

    public Token(
            final String textRepresentation,
            final int begin,
            final int end,
            final TokenType tokenType,
            final Runnable onClick
    ) {
        this.textRepresentation = textRepresentation;
        this.begin = begin;
        this.end = end;
        this.tokenType = tokenType;
        this.onClick = onClick;
    }

    public Text asTextElement() {
        if (TokenType.SIMPLE_TEXT.equals(tokenType)) {
            return new Text(textRepresentation);
        } else {
            return new ClickableText(textRepresentation, onClick);
        }
    }

    public String getTextRepresentation() {
        return textRepresentation;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    /**
     * Simple enum flag for whether a given {@link Token} is actually plain text or a URL.
     */
    public enum TokenType {
        SIMPLE_TEXT,
        CLICKABLE
    }

}
