package moe.lyrebird.model.util;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;

public final class FXProperties {

    private FXProperties() {
    }

    public static <T> void waitForProp(final ObservableValue<T> prop, final Consumer<T> onReady) {
        if (prop.getValue() == null) {
            prop.addListener((observable, oldValue, newValue) -> {
                onReady.accept(newValue);
            });
        } else {
            onReady.accept(prop.getValue());
        }
    }

    public static void waitForBooleanProp(final ObservableValue<Boolean> prop, final Runnable onReady) {
        if (prop.getValue() != null && prop.getValue()) {
            onReady.run();
        } else {
            prop.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    onReady.run();
                }
            });
        }
    }

}
