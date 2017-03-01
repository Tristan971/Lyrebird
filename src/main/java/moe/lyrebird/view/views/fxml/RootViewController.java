package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.ViewLoader;
import moe.lyrebird.view.views.Views;
import moe.lyrebird.view.views.utils.StageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@SuppressWarnings("unused")
@Component
@Slf4j
public class RootViewController {
    private final ViewLoader viewLoader;
    @FXML
    private Button loginButton;
    
    @Autowired
    public RootViewController(final ViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }
    
    public void initialize() {
        this.loginButton.addEventHandler(MouseEvent.MOUSE_RELEASED, this::loadLoginWindow);
    }
    
    private void loadLoginWindow(final MouseEvent event) {
        log.info("User requested login.");
        final Scene loginScene = this.viewLoader.loadScene(Views.LOGIN_VIEW);
        final Stage loginStage = StageUtils.stageOf("Login", loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.show();
    }
}
