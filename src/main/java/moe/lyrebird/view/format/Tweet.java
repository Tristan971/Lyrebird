package moe.lyrebird.view.format;

import lombok.experimental.UtilityClass;
import twitter4j.Status;

/**
 * Created by tristan on 03/03/2017.
 */
@UtilityClass
public class Tweet {
    public static String of(final Status status) {
        return String.format(
                "@%s : %s",
                status.getUser().getName(),
                status.getText()
        );
    }
}
