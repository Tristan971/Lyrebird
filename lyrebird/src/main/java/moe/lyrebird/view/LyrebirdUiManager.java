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

package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlNode;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.spring.application.FxUiManager;
import moe.lyrebird.model.notifications.Notification;
import moe.lyrebird.model.notifications.NotificationService;
import moe.lyrebird.view.screens.Screen;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@link LyrebirdUiManager} is responsible for bootstraping the GUI of the application correctly.
 * <p>
 * To do so, it serves as the entry point for the JavaFX side of things and uses overriding from {@link FxUiManager} for
 * root view configuration.
 */
@Component
public class LyrebirdUiManager extends FxUiManager {

    private final StageManager stageManager;
    private final Environment environment;
    private final NotificationService notificationService;

    private final AtomicBoolean informedUserOfCloseBehavior = new AtomicBoolean(false);

    /**
     * The constructor of this class, called by Spring automatically.
     *
     * @param easyFxml            The instance of {@link EasyFxml} that will be used to load the main stage's view.
     * @param stageManager        The instance of {@link StageManager} that will be used to register the root view for
     *                            later retrieval.
     * @param environment         The spring environment to use property keys for minimal size and main stage title.
     * @param notificationService The notification service that will be used to notify user of the custom behavior of
     *                            this main stage's closure view {@link #handleMainStageClosure(Stage)}.
     */
    @Autowired
    public LyrebirdUiManager(
            final EasyFxml easyFxml,
            final StageManager stageManager,
            final Environment environment,
            final NotificationService notificationService
    ) {
        super(easyFxml);
        this.stageManager = stageManager;
        this.environment = environment;
        this.notificationService = notificationService;
    }

    /**
     * @return the title of the main window
     */
    @Override
    protected String title() {
        return String.format(
                "%s [%s]",
                environment.getRequiredProperty("app.promo.name"),
                environment.getRequiredProperty("app.version")
        );
    }

    /**
     * This method is called by {@link FxUiManager} and is the first call that is made after {@link
     * FxUiManager#startGui(Stage)} is called.
     * <p>
     * Here we mostly do some post-processing to:
     * <ul>
     * <li>Disable closure of the application on closure of main stage</li>
     * <li>Treating closure of main stage as a request to hide it</li>
     * <li>Set-up minimum stage size contraints</li>
     * <li>Register it agains {@link StageManager} for retrieval in various places</li>
     * </ul>
     *
     * @param mainStage The application's main stage.
     */
    @Override
    protected void onStageCreated(final Stage mainStage) {
        Platform.setImplicitExit(false);
        mainStage.setOnCloseRequest(e -> handleMainStageClosure(mainStage));
        mainStage.setMinHeight(environment.getRequiredProperty("mainStage.minHeigth", Integer.class));
        mainStage.setMinWidth(environment.getRequiredProperty("mainStage.minWidth", Integer.class));
        stageManager.registerSingle(Screen.ROOT_VIEW, mainStage);
    }

    /**
     * @return The root {@link FxmlNode} of the application to put inside the main stage as sole node of the main scene.
     */
    @Override
    protected FxmlNode mainComponent() {
        return Screen.ROOT_VIEW;
    }

    /**
     * This method is called when the main stage is closed (i.e. the user clicks on the close window button).
     *
     * @param mainStage A reference to the main stage. Mostly here to avoid creating a field only for this method.
     */
    private void handleMainStageClosure(final Stage mainStage) {
        mainStage.hide();
        if (!informedUserOfCloseBehavior.getAcquire()) {
            notificationService.sendNotification(new Notification(
                    "Lyrebird's main window closed",
                    "Exiting the main window does not close Lyrebird, use the icon in the system tray."
            ));
            informedUserOfCloseBehavior.setRelease(true);
        }
    }

}
