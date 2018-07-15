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
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.view.screens.Screens;
import twitter4j.User;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

@Component
public class UserDetailsService {

    private final EasyFxml easyFxml;

    private final Property<User> targetUser = new SimpleObjectProperty<>(null);

    @Autowired
    public UserDetailsService(final EasyFxml easyFxml) {
        this.easyFxml = easyFxml;
    }

    public Property<User> targetUserProperty() {
        return targetUser;
    }

    public void openUserDetails(final User targetUser) {
        this.targetUser.setValue(targetUser);
        final FxmlLoadResult<Pane, FxmlController> userDetailsLoad = easyFxml.loadNode(Screens.USER_VIEW);
        final Pane userDetailsPane = userDetailsLoad.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        Stages.stageOf(targetUser.getName() + " (@" + targetUser.getScreenName() + ")", userDetailsPane)
              .thenAcceptAsync(Stages::scheduleDisplaying);
    }

}
