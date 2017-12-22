package moe.lyrebird.view.views.fxml;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.GUIManager;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.FxAsyncUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static moe.lyrebird.view.views.Views.CONTROL_BAR;
import static moe.lyrebird.view.views.Views.TIMELINE_VIEW;

/**
 * The RootViewController manages the location of content
 * on the root view scene.
 */
@Component
@Slf4j
public class RootViewController implements FxmlController {
    private final GUIManager guiManager;
    private final StageManager stageManager;

    @FXML
    private BorderPane contentPane;
    
    @Autowired
    public RootViewController(final GUIManager guiManager, final StageManager stageManager) {
        this.guiManager = guiManager;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize() {
        loadControlBar();
        loadTimeline();
    }

    private void loadControlBar() {
        log.info("Initializing control bar...");
        final Pane controlBarPane = this.guiManager.getEasyFxml()
                .loadNode(CONTROL_BAR)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());
        log.info("Initialized control bar !");
        log.info("Displaying control bar...");
        FxAsyncUtils.doOnFxThread(contentPane, root -> root.setLeft(controlBarPane));
    }

    private void loadTimeline() {
        log.info("Loading timeline view.");
        final Pane timelinePane = this.guiManager.getEasyFxml()
                .loadNode(TIMELINE_VIEW)
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        this.contentPane.setCenter(timelinePane);
        log.info("Loaded timeline view.");
    }
}
