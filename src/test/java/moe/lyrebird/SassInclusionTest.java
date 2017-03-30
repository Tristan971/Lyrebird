package moe.lyrebird;

import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.PathUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Created by Tristan on 30/03/2017.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SassInclusionTest {
    @Test
    public void testCssLocation() throws IOException {
        final Path cssFilePath = PathUtils.getPathForResource("lyrebird.css");
        Assert.assertNotNull(cssFilePath);
        log.info(
                "CSS file : {}",
                '\n' + Files.lines(cssFilePath)
                        .collect(Collectors.joining("\n"))
        );
    }
}
