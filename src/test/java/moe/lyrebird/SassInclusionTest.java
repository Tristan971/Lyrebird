package moe.lyrebird;

import moe.tristan.easyfxml.util.Resources;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Tristan on 30/03/2017.
 */
@Slf4j
public class SassInclusionTest {

    @Test
    public void testCssLocation() throws IOException {
        final Try<Path> cssFilePath = Resources.getResourcePath("../classes/lyrebird.css");
        assertThat(cssFilePath.isSuccess()).isTrue();

        log.info(
                "CSS file : {}",
                '\n' + Files.lines(cssFilePath.get())
                            .collect(Collectors.joining("\n"))
        );
    }

}
