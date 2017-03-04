package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.StageUtils;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@Component
@Slf4j
public class RootViewController {
    private final ViewLoader viewLoader;

    @FXML
    private Button loginButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Pane contentPane;
    
    @Autowired
    public RootViewController(final ViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }
    
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_RELEASED, this::openLoginWindow);
        this.timelineButton.addEventHandler(MOUSE_RELEASED, this::loadTimeline);
    }
    
    @SuppressWarnings("unused")
    private void openLoginWindow(final MouseEvent event) {
        log.info("User requested login.");
        final Scene loginScene = this.viewLoader.loadScene(Views.LOGIN_VIEW);
        final Stage loginStage = StageUtils.stageOf("Login", loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.show();
    }
    
    @SuppressWarnings("unused")
    private void loadTimeline(final MouseEvent event) {
        log.info("Loading timeline.");
        final Pane timelinePane = this.viewLoader.loadPane(Views.TIMELINE_VIEW);
        this.contentPane.getChildren().add(timelinePane);
    }
}
