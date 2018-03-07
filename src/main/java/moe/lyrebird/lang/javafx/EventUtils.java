package moe.lyrebird.lang.javafx;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@UtilityClass
public final class EventUtils {

    @Contract(pure = true)
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
