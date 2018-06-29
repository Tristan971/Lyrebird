package moe.lyrebird.model.twitter.services;

import org.springframework.stereotype.Component;
import io.vavr.CheckedFunction1;
import moe.lyrebird.model.sessions.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.UploadedMedia;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

@Component
public class NewTweetService {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetService.class);

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final SessionManager sessionManager;

    public NewTweetService(SessionManager sessionManager) {
        LOG.debug("Initializing {}...", getClass().getSimpleName());
        this.sessionManager = sessionManager;
    }

    public void sendNewTweet(final String content, final List<File> medias) {
        sessionManager.getCurrentTwitter()
                      .andThenTry(twitter -> {
                          final List<Long> mediaIds = uploadMedias(twitter, medias);
                          twitter.updateStatus(buildStatus(content, mediaIds));
                      });
    }

    private StatusUpdate buildStatus(final String content, final List<Long> mediaIds) {
        LOG.debug("Preparing new tweet with content [\"{}\"] and mediaIds {}", content, mediaIds);
        final StatusUpdate statusUpdate = new StatusUpdate(content);

        final Long[] mediaIdsArr = mediaIds.toArray(new Long[0]);
        final long[] mediaUnboxedArr = new long[mediaIdsArr.length];
        System.arraycopy(mediaIdsArr, 0, mediaUnboxedArr, 0, mediaIdsArr.length);
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
