package moe.lyrebird;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.testfx.framework.junit.ApplicationTest;

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
