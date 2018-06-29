package moe.lyrebird;

import org.springframework.util.StreamUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tristan on 30/03/2017.
 */
public class SassInclusionTest {

    private static final Logger LOG = LoggerFactory.getLogger(SassInclusionTest.class);

    @Test
    public void testCssLocation() throws IOException {
        final InputStream cssURL = getClass().getClassLoader().getResourceAsStream("style/lyrebird.css");
        final String cssContent = StreamUtils.copyToString(cssURL, StandardCharsets.UTF_8);

        LOG.info("CSS file : {}", cssContent);
    }

}
