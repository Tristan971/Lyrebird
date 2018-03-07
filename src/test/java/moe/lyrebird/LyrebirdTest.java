package moe.lyrebird;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;

@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class LyrebirdTest extends ApplicationTest {
    private static Stage stage;
    @Autowired
    private ApplicationContext context;

    @Test
    public void start() {
        final Lyrebird lyrebird = this.context.getBean(Lyrebird.class);
        lyrebird.init();
        Platform.runLater(() -> lyrebird.start(stage));
    }

    @Override
    public void start(final Stage stage) {
        LyrebirdTest.stage = stage;
    }
}
