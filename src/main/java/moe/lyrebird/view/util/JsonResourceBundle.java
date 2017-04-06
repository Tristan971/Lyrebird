package moe.lyrebird.view.util;

import moe.lyrebird.lang.collections.EnumerationUtils;
import moe.lyrebird.model.io.ClasspathPathLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

/**
 * Created by Tristan on 27/03/2017.
 */
public class JsonResourceBundle extends ResourceBundle {
    
    private final Map<String, Object> properties = new ConcurrentHashMap<>();
    
    public JsonResourceBundle(final String resourceBundleName, final Locale locale) {
        final Path resourceBundleToLoadPath = JsonResourceBundle.getPathForBundle(resourceBundleName, locale);
        this.loadResourceBundleAtPath(resourceBundleToLoadPath);
    }
    
    @Override
    protected Object handleGetObject(@NotNull final String key) {
        return this.properties.getOrDefault(key, null);
    }
    
    @NotNull
    @Override
    public Enumeration<String> getKeys() {
        final Iterator<String> keys = this.properties.keySet().iterator();
        return EnumerationUtils.enumerationOf(keys::hasNext, keys::next);
    }
    
    private void loadResourceBundleAtPath(final Path path) {
    
    }
    
    private static Path getPathForBundle(final String resourceBundleName, final Locale locale) {
        final String pathStr = String.format(
                "translations/%s_%s",
                resourceBundleName,
                locale.toString()
        );
        
        final Path filePath = ClasspathPathLoader.getPathForResource(pathStr);
        
        if (nonNull(filePath)) {
            return filePath;
        } else {
            final String pathStrFallback = pathStr.split("_")[0];
            return ClasspathPathLoader.getPathForResource(pathStr);
        }
    }
}
