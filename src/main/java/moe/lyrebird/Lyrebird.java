package moe.lyrebird;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import moe.tristan.easyfxml.spring.application.FxSpringApplication;
import moe.tristan.easyfxml.spring.application.FxSpringContext;
import moe.lyrebird.model.interrupts.CleanupService;

/**
 * Main application entry point.
 */
@SpringBootApplication
@Import(FxSpringContext.class)
public class Lyrebird extends FxSpringApplication {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        springContext.getBean(CleanupService.class).executeCleanupOperations();
        super.stop();
    }
}
