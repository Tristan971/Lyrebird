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

package moe.lyrebird.view.util;

import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides helper methods for filtering out URLs in text and extracting them out.
 */
public final class HyperlinkUtils {

    private static final String URL_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    private HyperlinkUtils() {
        throw new UnsupportedOperationException("Should not be instanciated.");
    }

    /**
     * Transforms all the URLs in an input String.
     *
     * @param input    The input string
     * @param replacer The transformation function
     *
     * @return The resulting string
     */
    private static String transformUrls(final String input, final Function<String, String> replacer) {
        return URL_PATTERN.matcher(input).replaceAll(match -> replacer.apply(match.group()));
    }

    /**
     * Extracts all the URLs from the given input String.
     *
     * @param input The input string
     *
     * @return The list of all URLs matching {@link #URL_REGEX} in the input
     */
    public static List<String> findAllUrls(final String input) {
        return URL_PATTERN.matcher(input).results().map(MatchResult::group).collect(Collectors.toList());
    }

    /**
     * Strips/trims all the URLs in the input String
     *
     * @param input The input string
     *
     * @return The resulting string
     */
    public static String stripAllUrls(final String input) {
        return transformUrls(input, url -> "");
    }

}
