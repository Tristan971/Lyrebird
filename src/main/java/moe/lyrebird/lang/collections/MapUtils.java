package moe.lyrebird.lang.collections;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Created by Tristan on 04/03/2017.
 */
@UtilityClass
public class MapUtils {
    @NonNull
    public static <K, V> Map.Entry<K, V> entryFor(@NonNull final K key, @NonNull final Map<K, V> map) {
        return new Map.Entry<K, V>() {
            @Override
            public K getKey() {
                return key;
            }
            
            @Override
            public V getValue() {
                return map.get(key);
            }
            
            @Override
            public V setValue(final V value) {
                return map.put(key, value);
            }
        };
    }
}
