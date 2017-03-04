package moe.lyrebird.lang;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * This class helps with stubs or other types of elements that
 * require non-null casted elements temporarily.
 * Use with caution.
 */
@Slf4j
@UtilityClass
public class Dummy {
    /**
     * Returns a dummy object of some class.
     *
     * @param tClass
     *         The class queried. Needs to be an instantiable class. (i.e. have a public no-arg constructor)
     * @param <T>
     *         The type of the required class
     * @return A false object of class T.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDummy(final Class<T> tClass) {
        try {
            final Constructor<T> defaultConstructor = tClass.getConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Can not be used with classes that are not instantiable.");
        }
    }
}
