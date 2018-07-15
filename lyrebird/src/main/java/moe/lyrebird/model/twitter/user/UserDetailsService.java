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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.view.screens.Screens;
import moe.lyrebird.view.screens.user.UserViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.User;

import javafx.scene.layout.Pane;

@Component
public class UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);

    private final EasyFxml easyFxml;

    @Autowired
    public UserDetailsService(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    public void openUserDetails(final User targetUser) {
        LOG.info("Opening detailed view of user : {} (@{})", targetUser.getName(), targetUser.getScreenName());
        final FxmlLoadResult<Pane, UserViewController> userDetailsLoad = easyFxml.loadNode(
                Screens.USER_VIEW,
                Pane.class,
                UserViewController.class
        );
        userDetailsLoad.getController()
                       .onFailure(err -> LOG.error("Could not load user details view", err))
                       .onSuccess(uvc -> uvc.targetUserProperty().setValue(targetUser));
        final Pane userDetailsPane = userDetailsLoad.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        Stages.stageOf(targetUser.getName() + " (@" + targetUser.getScreenName() + ")", userDetailsPane)
              .thenAcceptAsync(Stages::scheduleDisplaying);
    }

}