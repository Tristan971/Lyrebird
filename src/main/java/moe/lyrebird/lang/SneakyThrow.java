package moe.lyrebird.lang;

import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tristan on 01/03/2017.
 */
@UtilityClass
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
    
    public static <T, R> Function<T, R> unchecked(final ThrowingFunction<T, R> originalFunction) {
        return element -> {
            try {
                return originalFunction.apply(element);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
    
    public interface ThrowingFunction<T, R> {
        R apply(T elem) throws Exception;
    }
}
