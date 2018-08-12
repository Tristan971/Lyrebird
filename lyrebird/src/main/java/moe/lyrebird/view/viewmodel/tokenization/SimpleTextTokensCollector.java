package moe.lyrebird.view.viewmodel.tokenization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import twitter4a.Status;

public final class SimpleTextTokensCollector {

    private SimpleTextTokensCollector() {
    }

    public static List<Token> collectLeftovers(final Status status, final List<Token> tokens) {
        final String text = status.getText();

        if (tokens.isEmpty()) {
            return Collections.singletonList(asToken(text, 0, text.length()));
        }

        tokens.sort(Comparator.comparingInt(Token::getBegin));

        final List<Token> textTokens = new ArrayList<>(tokens.size() * 2);

        int cursorLeft = 0;
        int cursorRight = 0;
        for (final Token token : tokens) {
            final int tokenBegin = token.getBegin();
            final int tokenEnd = token.getEnd();

            cursorRight = tokenBegin;
            if (cursorRight > cursorLeft) {
                textTokens.add(asToken(text.substring(cursorLeft, cursorRight), cursorLeft, cursorRight));
            }
            cursorRight = tokenEnd;
            cursorLeft = tokenEnd;
        }

        if (cursorRight < text.length() - 1) {
            textTokens.add(asToken(text.substring(cursorLeft), cursorLeft, text.length()));
        }

        return textTokens;
    }

    private static Token asToken(final String text, final int begin, final int end) {
        return new Token(
                text,
                text,
                begin,
                end,
                Token.TokenType.SIMPLE_TEXT,
                () -> {
                }
        );
    }

}
