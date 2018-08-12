package moe.lyrebird.view.viewmodel.tokenization;

import javafx.scene.text.Text;

import moe.lyrebird.view.viewmodel.javafx.ClickableText;

/**
 * This class represents a String-convertible token that optionally triggers an action on click.
 */
public final class Token {

    private final String textRepresentation;
    private final int begin;
    private final int end;
    private final Runnable onClick;

    public Token(final String textRepresentation, final int begin, final int end, final Runnable onClick) {
        this.textRepresentation = textRepresentation;
        this.begin = begin;
        this.end = end;
        this.onClick = onClick;
    }

    public Token(final String textRepresentation, final int begin, final int end) {
        this(textRepresentation, begin, end, () -> {});
    }

    public Text asTextElement() {
        return new ClickableText(textRepresentation, onClick);
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

}

