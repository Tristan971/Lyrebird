package moe.lyrebird.view.util;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.experimental.UtilityClass;

/**
 * Created by Tristan on 01/03/2017.
 */
@UtilityClass
public class StageUtils {
    public static Stage stageOf(final String title, final Pane pane) {
        return stageOf(title, pane, true);
    }
    
    public static Stage stageOf(final String title, final Pane pane, final boolean enableTheming) {
        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.setScene(new Scene(pane));
        return stage;
    }
}
