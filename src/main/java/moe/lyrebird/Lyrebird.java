package moe.lyrebird;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GuiBootstraper;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application entry point.
 */
@Slf4j
@SpringBootApplication
public class Lyrebird extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        this.context = SpringApplication.run(Lyrebird.class);
    }

    @Override
    public void start(final Stage primaryStage) {
        this.context.getBean(GuiBootstraper.class).startGui(primaryStage);
    }

    @Override
    public void stop() {
        this.context.stop();
    }

    public static void main(final String... args) {
        Application.launch(args);
    }

}

