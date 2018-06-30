package moe.lyrebird.model.twitter.services.interraction;

import twitter4j.Status;

import java.util.function.BiFunction;

public enum Interration implements TweetInterraction {
    LIKE(
            TweetInterractionService::like,
            TweetInterractionService::unlike,
            TweetInterractionService::shouldLike
    ),
    RETWEET(
            TweetInterractionService::retweet,
            TweetInterractionService::unretweet,
            TweetInterractionService::shouldRetweet
    );

    private final BiFunction<TweetInterractionService, Status, Status> onTrue;
    private final BiFunction<TweetInterractionService, Status, Status> onFalse;
    private final BiFunction<TweetInterractionService, Status, Boolean> shouldDo;

    Interration(
            final BiFunction<TweetInterractionService, Status, Status> onTrue,
            final BiFunction<TweetInterractionService, Status, Status> onFalse,
            final BiFunction<TweetInterractionService, Status, Boolean> shouldDo
    ) {
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        this.shouldDo = shouldDo;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Status> onTrue() {
        return onTrue;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Status> onFalse() {
        return onFalse;
    }

    @Override
    public BiFunction<TweetInterractionService, Status, Boolean> shouldDo() {
        return shouldDo;
    }
}
