package moe.lyrebird;

import javafx.application.Application;
import javafx.stage.Stage;
import moe.lyrebird.view.GUIManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main application entry point.
 */
@SpringBootApplication
public class Lyrebird extends Application {
    private ConfigurableApplicationContext context;
    
    @Override
    public void init() throws Exception {
        this.context = SpringApplication.run(Lyrebird.class);
    }
    
    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.context.registerShutdownHook();
        this.context.getBean(GUIManager.class).startGui(primaryStage);
    }
    
    @Override
    public void stop() throws Exception {
        this.context.stop();
    }
    
    public static void main(final String... args) {
        Application.launch(args);
    }
    
}

