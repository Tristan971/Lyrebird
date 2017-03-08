package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GUIManager;
import moe.lyrebird.view.util.StageUtils;
import moe.lyrebird.view.views.Controller;
import moe.lyrebird.view.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@Component
@Slf4j
public class RootViewController implements Controller {
    private final GUIManager guiManager;

    @FXML
    private Button loginButton;
    @FXML
    private Button timelineButton;
    @FXML
    private Pane contentPane;
    
    @Autowired
    public RootViewController(final ApplicationContext context) {
        this.guiManager = context.getBean(GUIManager.class);
    }
    
    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_RELEASED, event -> this.openLoginWindow());
        this.timelineButton.addEventHandler(MOUSE_RELEASED, event -> this.loadTimeline());
    }
    
    private void openLoginWindow() {
        log.info("User requested login.");
        final Scene loginScene = this.guiManager.getViewLoader().loadScene(Views.LOGIN_VIEW);
        final Stage loginStage = StageUtils.stageOf("Login", loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.show();
        loginStage.setOnShown(shownEvent -> this.guiManager.registerStage(LoginViewController.class, loginStage));
    }
    
    private void loadTimeline() {
        log.info("Loading timeline.");
        final Pane timelinePane = this.guiManager.getViewLoader().loadPane(Views.TIMELINE_VIEW);
        this.contentPane.getChildren().add(timelinePane);
    }
}
