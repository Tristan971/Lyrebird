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

package moe.lyrebird.model.twitter.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javafx.scene.layout.Pane;

import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.view.screens.user.UserScreenComponent;
import moe.lyrebird.view.screens.user.UserViewController;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Stages;

import io.vavr.control.Try;
import twitter4j.User;

/**
 * This service serves as a helper for displaying a given user's detailed view.
 *
 * @see UserScreenComponent
 */
@Component
public class UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);

    private final EasyFxml easyFxml;
    private final SessionManager sessionManager;

    private final UserScreenComponent userScreenComponent;

    @Autowired
    public UserDetailsService(EasyFxml easyFxml, SessionManager sessionManager, UserScreenComponent userScreenComponent) {
        this.easyFxml = easyFxml;
        this.sessionManager = sessionManager;
        this.userScreenComponent = userScreenComponent;
    }

    public void openUserDetails(final String screenName) {
        findUser(screenName)
                .onSuccess(this::openUserDetails)
                .onFailure(err -> ExceptionHandler.displayExceptionPane(
                        "Unknown user!",
                        "Can't map this user's screen name (@...) to an actual Twitter user!",
                        err
                ));
    }

    public void openUserDetails(final long targetUserId) {
        findUser(targetUserId)
                .onSuccess(this::openUserDetails)
                .onFailure(err -> ExceptionHandler.displayExceptionPane(
                        "Unknown user!",
                        "Can't map this user's userId to an actual Twitter user!",
                        err
                ));
    }

    /**
     * Opens the detailed view of a given user.
     *
     * @param targetUser the user whose detailed view is requested to be shown
     */
    public void openUserDetails(final User targetUser) {
        LOG.info("Opening detailed view of user : {} (@{})", targetUser.getName(), targetUser.getScreenName());
        easyFxml.load(userScreenComponent, Pane.class, UserViewController.class)
                .afterControllerLoaded(uvc -> uvc.targetUserProperty().setValue(targetUser))
                .getNode()
                .recover(ExceptionHandler::fromThrowable)
                .onSuccess(userDetailsPane -> {
                    final String stageTitle = targetUser.getName() + " (@" + targetUser.getScreenName() + ")";
                    Stages.stageOf(stageTitle, userDetailsPane).thenAcceptAsync(Stages::scheduleDisplaying);
                });
    }

    @Cacheable("findByScreenNameUserCache")
    public Try<User> findUser(final String userScreenName) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.showUser(userScreenName));
    }

    @Cacheable("findByUserIdUserCache")
    public Try<User> findUser(final long userId) {
        return sessionManager.doWithCurrentTwitter(twitter -> twitter.showUser(userId));
    }

}
