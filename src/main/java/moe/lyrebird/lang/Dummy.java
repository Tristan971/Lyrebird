package moe.lyrebird.lang;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * This class helps with stubs or other types of elements that
 * require non-null casted elements temporarily.
 * Use with caution.
 */
@Slf4j
@UtilityClass
public class Dummy {
    /**
     * Returns a dummy object of some class
     *
     * @param tClass
     *         The class queried
     * @param <T>
     *         The object
     * @return A false object of class T. Often will break with new APIs.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDummy(final Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return (T) new Object();
        }
    }
}
