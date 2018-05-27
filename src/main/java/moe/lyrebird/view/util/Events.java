package moe.lyrebird.view.util;

import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class Events {

    private Events() {}

    public static KeyEvent dummyKeyEvent(final EventType<KeyEvent> eventType) {
        return new KeyEvent(
                eventType,
                KeyEvent.CHAR_UNDEFINED,
                "dummy character",
                KeyCode.UNDEFINED,
                false,
                false,
                false,
                false
        );
    }
}
