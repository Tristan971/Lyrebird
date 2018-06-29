package moe.lyrebird.model.twitter;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javafx.stage.FileChooser.ExtensionFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TwitterMediaExtensionFilter {

    public final ExtensionFilter extensionFilter;

    public TwitterMediaExtensionFilter(Environment environment) {
        this.extensionFilter = buildExtensionFilter(environment);
    }

    private static ExtensionFilter buildExtensionFilter(final Environment environment) {
        final String allowedExtensionsStr = environment.getRequiredProperty("twitter.media.allowedExtensions");
        final List<String> allowedExtensions = Arrays.stream(allowedExtensionsStr.split(","))
                                     .map(ext -> "*." + ext)
                                     .collect(Collectors.toList());

        return new ExtensionFilter("Supported twitter medias", allowedExtensions);
    }

}
