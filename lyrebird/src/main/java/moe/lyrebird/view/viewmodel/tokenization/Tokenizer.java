package moe.lyrebird.view.viewmodel.tokenization;

import java.util.List;

import twitter4a.Status;

public interface Tokenizer {

    List<Token> tokenize(final Status status);

}
