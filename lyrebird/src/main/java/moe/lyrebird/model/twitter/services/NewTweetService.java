/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird.model.twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

/**
 * This service handles tasks related to posting a new tweet
 */
@Component
public class NewTweetService {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetService.class);

    private static final Executor TWITTER_EXECUTOR = Executors.newSingleThreadExecutor();

    private final SessionManager sessionManager;

    @Autowired
    public NewTweetService(final SessionManager sessionManager) {
        LOG.debug("Initializing {}...", getClass().getSimpleName());
        this.sessionManager = sessionManager;
    }

    /**
     * Asynchronously sends a normal tweet.
     *
     * @param content The content of the tweet
     * @param medias  The attachments to upload and link in the tweet
     *
     * @return A {@link CompletionStage} to follow the status of the asynchronous request
     */
    public CompletionStage<Status> sendTweet(final String content, final List<File> medias) {
        return prepareNewTweet(content, medias).thenApplyAsync(
                update -> sessionManager.doWithCurrentTwitter(twitter -> twitter.updateStatus(update)).get(),
                TWITTER_EXECUTOR
        );
    }

    /**
     * Asynchronously sends a reply to a tweet.
     *
     * @param content   The content of the reply
     * @param medias    The attachments to upload and link in the reply
     * @param inReplyTo The tweet that this is in reply to
     *
     * @return A {@link CompletionStage} to follow the status of the asynchronous request
     */
    public CompletionStage<Status> sendReply(final String content, final List<File> medias, final long inReplyTo) {
        return prepareNewTweet(content, medias).thenApplyAsync(
                update -> {
                    LOG.debug("Set inReplyTo for status update {} as : {}", update, inReplyTo);
                    update.setInReplyToStatusId(inReplyTo);
                    return update;
                }, TWITTER_EXECUTOR
        ).thenApplyAsync(
                update -> sessionManager.doWithCurrentTwitter(twitter -> twitter.updateStatus(update)).get(),
                TWITTER_EXECUTOR
        );
    }

    /**
     * Prepares a new tweet by uploading media related.
     *
     * @param content The textual content of the tweet
     * @param medias  The medias to embed in it
     *
     * @return A {@link CompletionStage} to monitor the request.
     */
    private CompletionStage<StatusUpdate> prepareNewTweet(final String content, final List<File> medias) {
        return CompletableFuture.supplyAsync(
                () -> sessionManager.doWithCurrentTwitter(twitter -> uploadMedias(twitter, medias)).get(),
                TWITTER_EXECUTOR
        ).thenApplyAsync(
                uploadedMediasIds -> buildStatus(content, uploadedMediasIds),
                TWITTER_EXECUTOR
        );
    }

    /**
     * Builds a {@link StatusUpdate} from given parameters
     *
     * @param content  The textual content of this update
     * @param mediaIds The mediaIds (from Twitter-side) to be embedded in it
     *
     * @return The resulting status update to post
     */
    private static StatusUpdate buildStatus(final String content, final List<Long> mediaIds) {
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

    /**
     * Uploads the given media files to Twitter
     *
     * @param twitter     The twitter instance to use for uploading
     * @param attachments The media files to upload
     *
     * @return The uploaded media files Twitter-side ids
     */
    private static List<Long> uploadMedias(final Twitter twitter, final List<File> attachments) {
        LOG.debug("Uploading media attachments {}", attachments);
        return attachments.stream()
                          .map(unchecked((CheckedFunction1<File, UploadedMedia>) twitter::uploadMedia))
                          .map(UploadedMedia::getMediaId)
                          .collect(Collectors.toList());
    }

}
