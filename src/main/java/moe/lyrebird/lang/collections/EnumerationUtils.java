package moe.lyrebird.lang.collections;

import lombok.experimental.UtilityClass;

import java.util.Enumeration;
import java.util.function.Supplier;

/**
 * Created by Tristan on 27/03/2017.
 */
@UtilityClass
public final class EnumerationUtils {
    public static <E> Enumeration<E> enumerationOf(final Supplier<Boolean> hasMoreElems, final Supplier<E> nextElem) {
        return new Enumeration<E>() {
            @Override
            public boolean hasMoreElements() {
                return hasMoreElems.get();
            }
            
            @Override
            public E nextElement() {
                return nextElem.get();
            }
        };
    }
}
