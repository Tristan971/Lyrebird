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

package moe.lyrebird.view.screens;

import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.api.FxmlFile;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.lyrebird.view.screens.credits.CreditsScreenController;
import moe.lyrebird.view.screens.login.LoginScreenController;
import moe.lyrebird.view.screens.newtweet.NewTweetController;
import moe.lyrebird.view.screens.root.RootScreenController;
import moe.lyrebird.view.screens.update.UpdateScreenController;
import moe.lyrebird.view.screens.user.UserViewController;

/**
 * The screens as an enum for easier autocompletion etc.
 */
@SuppressWarnings("unused")
public enum Screens implements FxmlNode {

    ROOT_VIEW("root/RootView.fxml", RootScreenController.class),
    LOGIN_VIEW("login/Login.fxml", LoginScreenController.class),
    NEW_TWEET_VIEW("newtweet/NewTweet.fxml", NewTweetController.class),
    CREDITS_VIEW("credits/Credits.fxml", CreditsScreenController.class),
    UPDATE_VIEW("update/Update.fxml", UpdateScreenController.class),
    USER_VIEW("user/UserView.fxml", UserViewController.class);

    private static final String SCREENS_BASE_PATH = "moe/lyrebird/view/screens/";

    private final String fxmlFile;
    private final Class<? extends FxmlController> controllerClass;

    Screens(final String fxmlFile, final Class<? extends FxmlController> controllerClass) {
        this.fxmlFile = fxmlFile;
        this.controllerClass = controllerClass;
    }

    @Override
    public FxmlFile getFile() {
        return () -> SCREENS_BASE_PATH + fxmlFile;
    }

    @Override
    public Class<? extends FxmlController> getControllerClass() {
        return controllerClass;
    }

}
