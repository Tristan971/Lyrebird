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
import moe.lyrebird.view.screens.Screens;

import javafx.stage.Stage;

/**
 * The {@link LyrebirdUiManager} is responsible for bootstraping the GUI of the application correctly.
 */
@Component
public class LyrebirdUiManager extends FxUiManager {

    private final StageManager stageManager;
    private final Environment environment;

    @Autowired
    public LyrebirdUiManager(
            final EasyFxml easyFxml,
            StageManager stageManager,
            final Environment environment
    ) {
        super(easyFxml);
        this.stageManager = stageManager;
        this.environment = environment;
    }

    @Override
    protected String title() {
        return String.format(
                "%s [%s]",
                "Lyrebird",
                environment.getProperty("app.version")
        );
    }

    @Override
    protected void onStageCreated(final Stage mainStage) {
        mainStage.setMinHeight(environment.getRequiredProperty("mainStage.minHeigth", Integer.class));
        mainStage.setMinWidth(environment.getRequiredProperty("mainStage.minWidth", Integer.class));
        stageManager.registerSingle(Screens.ROOT_VIEW, mainStage);
    }

    @Override
    protected FxmlNode mainComponent() {
        return Screens.ROOT_VIEW;
    }

}
