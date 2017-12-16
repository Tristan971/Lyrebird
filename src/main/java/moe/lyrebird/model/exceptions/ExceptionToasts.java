package moe.lyrebird.model.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ExceptionToasts {

    private static final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    public void submit(final String errorMessage) {
        log.debug("Received error message : {}", errorMessage);
        scheduler.submit(() -> log.error(errorMessage));
    }
}
