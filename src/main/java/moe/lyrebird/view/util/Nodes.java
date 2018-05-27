package moe.lyrebird.view.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public final class Nodes {

    private Nodes() {}

    public static void autoresizeContainerOn(
            final Node node,
            final ObservableValue<?> observableValue
    ) {
        observableValue.addListener((observable, oldValue, newValue) -> node.autosize());
    }

    public static void bindContentBiasCalculationTo(
            final Node node,
            final ObservableValue<? extends Boolean> observableValue
    ) {
        node.visibleProperty().bind(observableValue);
        node.managedProperty().bind(node.visibleProperty());
    }

}
