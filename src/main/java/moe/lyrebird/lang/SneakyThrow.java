package moe.lyrebird.lang;

import io.vavr.control.Try;
import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tristan on 01/03/2017.
 */
@UtilityClass
public class SneakyThrow {
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
    
    @SuppressWarnings({"unchecked"})
    public static <T> Try<T> tryUnchecked(final ThrowingSupplier<? extends T> supplier) {
        try {
            return Try.success(supplier.get());
        } catch (final Exception e) {
            return Try.failure(e);
        }
    }
    
    @SuppressWarnings("unused")
    public static <T, R> Function<T, R> uncheckedFun(final ThrowingFunction<T, R> originalFunction) {
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
