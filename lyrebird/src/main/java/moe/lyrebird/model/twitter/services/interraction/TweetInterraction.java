package moe.lyrebird.model.twitter.services.interraction;

import twitter4j.Status;

import java.util.function.BiFunction;

public interface TweetInterraction {
    BiFunction<TweetInterractionService, Status, Status> onTrue();
    BiFunction<TweetInterractionService, Status, Status> onFalse();
    BiFunction<TweetInterractionService, Status, Boolean> shouldDo();
}
