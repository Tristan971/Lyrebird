package moe.lyrebird.view.util;

import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HyperlinkUtils {

    private static final String URL_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static String transformUrls(final String input, final Function<String, String> replacer) {
        return URL_PATTERN.matcher(input).replaceAll(match -> replacer.apply(match.group()));
    }

    public static List<String> findAllUrls(final String input) {
        return URL_PATTERN.matcher(input).results().map(MatchResult::group).collect(Collectors.toList());
    }

    public static String stripAllUrls(final String input) {
        return URL_PATTERN.matcher(input).replaceAll("");
    }

}
