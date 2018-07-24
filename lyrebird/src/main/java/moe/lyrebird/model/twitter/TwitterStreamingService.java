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

package moe.lyrebird.model.twitter;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.interrupts.CleanupOperation;
import moe.lyrebird.model.interrupts.CleanupService;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterUserListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4a.Dispatcher;
import twitter4a.TwitterStream;

import javafx.beans.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for orchestration of the subscription to Twitter4J's streaming service.
 */
@Component
public class TwitterStreamingService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamingService.class);

    private final TwitterStream twitterStream;
    private final SessionManager sessionManager;
    private final CleanupService cleanupService;
    private final TwitterUserListener twitterUserListener;

    private final AtomicBoolean currentlyListening;

    public TwitterStreamingService(
            final TwitterStream twitterStream,
            final SessionManager sessionManager,
            final CleanupService cleanupService,
            final TwitterUserListener twitterUserListener
    ) {
        this.cleanupService = cleanupService;
        LOG.debug("Starting twitter stream connection...");
        this.currentlyListening = new AtomicBoolean(false);
        this.twitterStream = twitterStream;
        this.sessionManager = sessionManager;
        this.twitterUserListener = twitterUserListener;
        startListening();
    }

    /**
     * Starts listening to the streaming service
     */
    private void startListening() {
        LOG.debug("Preparing streaming service...");
        final Property<Session> currentSessionBinding = sessionManager.currentSessionProperty();
        currentSessionBinding.addListener((observable, oldValue, newValue) -> {
            this.currentlyListening.set(true);
            if (oldValue != null) {
                closeSession();
            }
            switchToSession(newValue);
        });

        if (!currentlyListening.get()) {
            if (currentSessionBinding.getValue() != null) {
                LOG.debug("Performing initial session initialization.");
                switchToSession(currentSessionBinding.getValue());
            } else {
                LOG.debug("Not logged in. Not starting streaming service.");
            }
        }

        cleanupService.registerCleanupOperation(new CleanupOperation(
                "Stop Twitter streaming listeners",
                this::closeSession
        ));
        cleanupService.registerCleanupOperation(new CleanupOperation(
                "Stop Twitter4J's internal dispatcher thread",
                twitterStream::shutdown
        ));
    }

    /**
     * Changes the user whose account activity we are streaming
     *
     * @param newSession The new user session to whose we will be listening for events about
     */
    private void switchToSession(final Session newSession) {
        LOG.info("Starting streaming for session {}", newSession);
        twitterStream.setOAuthAccessToken(newSession.getAccessToken());
        twitterStream.addListener(twitterUserListener);
        twitterStream.user();
    }

    /**
     * Stops listening to Twitter-side user events
     */
    private void closeSession() {
        LOG.info("\t\tStop Twitter4J streaming.");
        twitterStream.clearListeners();
        reflectForceCloseTwitter4J();
    }

    private void reflectForceCloseTwitter4J() {
        try {
            final Field handler = twitterStream.getClass().getDeclaredField("handler");
            handler.setAccessible(true);
            final Class<?> handlerClass = handler.get(twitterStream).getClass();
            final Method handlerCloseMethod = handlerClass.getMethod("close");
            handlerCloseMethod.setAccessible(true);
            handlerCloseMethod.invoke(handler.get(twitterStream));

            final Field dispatcherField = twitterStream.getClass().getDeclaredField("dispatcher");
            dispatcherField.setAccessible(true);
            final Dispatcher dispatcher = (Dispatcher) dispatcherField.get(null);
            dispatcher.shutdown();
        } catch (NoSuchFieldException e) {
            LOG.error("Cannot find dispatcher field in TwitterStream running implementation", e);
        } catch (IllegalAccessException e) {
            LOG.error("Cannot make handler field accessible.");
        } catch (NoSuchMethodException e) {
            LOG.error("Cannot find TwitterStreamConsumer#close method.");
        } catch (InvocationTargetException e) {
            LOG.error("Error while stopping the TwitterStreamConsumer.");
        }
    }

}
