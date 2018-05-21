package moe.lyrebird;

import org.springframework.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tristan on 30/03/2017.
 */
@Slf4j
public class SassInclusionTest {

    @Test
    public void testCssLocation() throws IOException {
        final InputStream cssURL = getClass().getClassLoader().getResourceAsStream("style/lyrebird.css");
        final String cssContent = StreamUtils.copyToString(cssURL, StandardCharsets.UTF_8);

        log.info("CSS file : {}", cssContent);
    }

}
