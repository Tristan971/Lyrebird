package moe.lyrebird.view.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * Created by Tristan on 31/03/2017.
 */
public class FXDOMUtils {
    public static void centerNode(final Node node, final Double marginSize) {
        AnchorPane.setTopAnchor(node, marginSize);
        AnchorPane.setBottomAnchor(node, marginSize);
        AnchorPane.setLeftAnchor(node, marginSize);
        AnchorPane.setRightAnchor(node, marginSize);
    }
}
