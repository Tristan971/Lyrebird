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

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Lazy
@Component
public class MarkdownRenderingService {

    private static final Logger LOG = LoggerFactory.getLogger(MarkdownRenderingService.class);

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public MarkdownRenderingService(final Parser markdownParser, final HtmlRenderer htmlRenderer) {
        this.markdownParser = markdownParser;
        this.htmlRenderer = htmlRenderer;
    }

    public String render(final String input) {
        LOG.debug("Input : {}", input);
        final Node hierarchy = markdownParser.parse(input);
        LOG.debug("Parsed AST : {}", hierarchy);
        final String html = htmlRenderer.render(hierarchy);
        LOG.debug("Output : {}", html);
        return html;
    }

}
