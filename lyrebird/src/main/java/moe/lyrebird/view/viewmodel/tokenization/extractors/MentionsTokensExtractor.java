package moe.lyrebird.view.viewmodel.tokenization.extractors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import moe.lyrebird.model.twitter.user.UserDetailsService;
import moe.lyrebird.view.viewmodel.tokenization.Token;
import moe.lyrebird.view.viewmodel.tokenization.TokensExtractor;

import twitter4a.Status;

@Component
public class MentionsTokensExtractor implements TokensExtractor {

    private final UserDetailsService userDetailsService;

    @Autowired
    public MentionsTokensExtractor(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public List<Token> extractTokens(final Status status) {
        return Arrays.stream(status.getUserMentionEntities()).map(mention -> new Token(
                "@" + mention.getText(),
                mention.getStart(),
                mention.getEnd(),
                () -> userDetailsService.openUserDetails(mention.getId())
        )).collect(Collectors.toList());
    }

}
