package moe.lyrebird.view.viewmodel.tokenization;

import java.util.List;

import twitter4a.Status;

public interface TokensExtractor {

    List<Token> extractTokens(final Status status);

}
