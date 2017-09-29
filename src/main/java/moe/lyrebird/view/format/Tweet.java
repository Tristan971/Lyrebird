package moe.lyrebird.view.format;

import lombok.experimental.UtilityClass;
import twitter4j.Status;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tristan on 03/03/2017.
 */
@UtilityClass
public class Tweet {
    
    public static List<String> ofStatuses(List<Status> statuses) {
        return statuses.stream().map(Tweet::of).collect(Collectors.toList());
    }
    
    public static String of(final Status status) {
        return String.format(
                "@%s : %s",
                status.getUser().getName(),
                status.getText()
        );
    }
}
