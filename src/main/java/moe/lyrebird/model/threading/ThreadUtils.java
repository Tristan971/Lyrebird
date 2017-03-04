package moe.lyrebird.model.threading;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by tristan on 04/03/2017.
 */
@SuppressWarnings("unused")
@UtilityClass
@Slf4j
public class ThreadUtils {
    private static final int nbcores;
    private static final ThreadPoolExecutor tpe;
    private static final ScheduledExecutorService stpe;

    static {
        nbcores = Runtime.getRuntime().availableProcessors();
        tpe = new ThreadPoolExecutor(
                1,
                nbcores,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(60)
        );

        stpe = new ScheduledThreadPoolExecutor(1);
    }

    public static void run(final Runnable runnable) {
        log.info("Submitted a task for execution.", runnable);
        tpe.submit(runnable);
    }

    public static <V> Future<V> run(final Callable<V> callable) {
        log.info("Submitted call for computation.", callable);
        return tpe.submit(callable);
    }

    public static void runlater(final Runnable runnable, final long time, final TimeUnit timeUnit) {
        log.info("Submitted task for execution in {} {}", runnable, time, timeUnit.toString());
        stpe.schedule(runnable, time, timeUnit);
    }

    public static <V> ScheduledFuture<V>  runLater(final Callable<V> callable, final long time, final TimeUnit timeUnit) {
        log.info("Submitted call for computation in {} {}", callable, time, timeUnit.toString());
        return stpe.schedule(callable, time, timeUnit);
    }
}
