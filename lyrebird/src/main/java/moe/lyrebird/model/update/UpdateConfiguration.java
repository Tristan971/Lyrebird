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

package moe.lyrebird.model.update;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Configuration relative to the update process
 */
@Configuration
public class UpdateConfiguration {

    /**
     * @return the configuration for the markdown to html renderer
     */
    @Bean
    public MutableDataSet flexmarkConfiguration() {
        return new MutableDataSet();
    }

    /**
     * @param flexmarkConfiguration The configuration to use (from {@link #flexmarkConfiguration()})
     * @return The configured markdown parser
     */
    @Bean
    public Parser markdownParser(final MutableDataSet flexmarkConfiguration) {
        return Parser.builder(flexmarkConfiguration).build();
    }

    /**
     * @param flexmarkConfiguration The configuration to use (from {@link #flexmarkConfiguration()})
     * @return The HTML renderer for markdown processing
     */
    @Bean
    public HtmlRenderer markdownRenderer(final MutableDataSet flexmarkConfiguration) {
        return HtmlRenderer.builder(flexmarkConfiguration).build();
    }

}
