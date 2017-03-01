package moe.lyrebird.lang;

import org.springframework.data.util.Pair;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tristan on 01/03/2017.
 */
public class SneakyThrow {
    public static final Throwable NO_EXCEPTION = new Throwable("No exception was thrown");
    
    public static <T> T unchecked(final ThrowingSupplier<? extends T> supplier) {
        return unchecked(supplier, RuntimeException::new).get();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Pair<T, Throwable> uncheckedWithException(final ThrowingSupplier<? extends T> supplier) {
        try {
            return Pair.of(supplier.get(), NO_EXCEPTION);
        } catch (final Exception e) {
            return Pair.of((T) new Object(), e);
        }
    }
    
    private static <T> Supplier<T> unchecked(final ThrowingSupplier<? extends T> supplier, final Function<? super Exception, ? extends RuntimeException> exceptionMapper) {
        return () -> {
            try {
                return supplier.get();
            } catch (final Exception exception) {
                throw exceptionMapper.apply(exception);
            }
        };
    }
    
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
