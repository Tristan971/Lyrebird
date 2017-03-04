package moe.lyrebird.lang;

import org.springframework.data.util.Pair;

import java.util.function.Supplier;

/**
 * Created by Tristan on 01/03/2017.
 */
public class SneakyThrow {
    public static final Throwable NO_EXCEPTION = new Throwable("No exception was thrown");
    
    public static <T> T unchecked(final ThrowingSupplier<? extends T> supplier) {
        final Supplier<T> safeSupplier = () -> {
            try {
                return supplier.get();
            } catch (final Exception exception) {
                throw new RuntimeException(exception);
            }
        };
        return safeSupplier.get();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Pair<T, Throwable> uncheckedWithException(final ThrowingSupplier<? extends T> supplier) {
        try {
            return Pair.of(supplier.get(), NO_EXCEPTION);
        } catch (final Exception e) {
            return Pair.of((T) new Object(), e);
        }
    }
    
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
