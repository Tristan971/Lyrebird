package moe.lyrebird.model.twitter.services;

import org.springframework.stereotype.Component;
import io.vavr.CheckedFunction1;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.UploadedMedia;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

@Component
public class NewTweetService {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetService.class);

    private static final ExecutorService TWEET_EXECUTOR = Executors.newSingleThreadExecutor();

    private final SessionManager sessionManager;

    public NewTweetService(SessionManager sessionManager) {
        LOG.debug("Initializing {}...", getClass().getSimpleName());
        this.sessionManager = sessionManager;
    }

    public CompletionStage<Status> sendNewTweet(final String content, final List<File> medias) {
        final CompletableFuture<Status> request = new CompletableFuture<>();

        TWEET_EXECUTOR.submit(() -> {
            sessionManager.getCurrentTwitter()
                          .mapTry(twitter -> {
                              final List<Long> mediaIds = uploadMedias(twitter, medias);
                              return twitter.updateStatus(buildStatus(content, mediaIds));
                          }).onSuccess(request::complete)
                          .onFailure(err -> {
                              LOG.error("Error posting tweet.", err);
                              request.completeExceptionally(err);
                          });
        });

        return request;
    }

    private StatusUpdate buildStatus(final String content, final List<Long> mediaIds) {
        LOG.debug("Preparing new tweet with content [\"{}\"] and mediaIds {}", content, mediaIds);
        final StatusUpdate statusUpdate = new StatusUpdate(content);

        final Long[] mediaIdsArr = mediaIds.toArray(new Long[0]);
        final long[] mediaUnboxedArr = new long[mediaIdsArr.length];
        for (int i = 0; i < mediaIdsArr.length; i++) {
            mediaUnboxedArr[i] = mediaIdsArr[i];
        }
        statusUpdate.setMediaIds(mediaUnboxedArr);

        return statusUpdate;
    }

    private List<Long> uploadMedias(Twitter twitter, final List<File> attachments) {
        return attachments.stream()
                          .map(unchecked((CheckedFunction1<File, UploadedMedia>) twitter::uploadMedia))
                          .map(UploadedMedia::getMediaId)
                          .collect(Collectors.toList());
    }

}
