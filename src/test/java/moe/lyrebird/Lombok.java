package moe.lyrebird;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;

/**
 * Created by Tristan on 04/03/2017.
 */
@UtilityClass
public final class Lombok {
    public static <T> void utilityClassTest(final Class<T> utilityClass) throws ReflectiveOperationException {
        final Constructor<T> illegalConstructor = utilityClass.getDeclaredConstructor();
        illegalConstructor.setAccessible(true);
        illegalConstructor.newInstance();
    }
}
