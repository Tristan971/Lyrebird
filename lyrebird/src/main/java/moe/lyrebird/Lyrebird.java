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

package moe.lyrebird;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import moe.tristan.easyfxml.spring.application.FxSpringApplication;
import moe.tristan.easyfxml.spring.application.FxSpringContext;
import moe.lyrebird.api.client.LyrebirdServerClientConfiguration;
import moe.lyrebird.model.interrupts.CleanupService;

/**
 * Main application entry point.
 */
@SpringBootApplication
@EnableCaching
@Import({FxSpringContext.class, LyrebirdServerClientConfiguration.class})
public class Lyrebird extends FxSpringApplication {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        springContext.getBean(CleanupService.class).executeCleanupOperations();
        super.stop();
        Runtime.getRuntime().halt(0);
    }
}
