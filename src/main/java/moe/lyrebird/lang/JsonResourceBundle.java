package moe.lyrebird.lang;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaslang.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.lang.collections.EnumerationUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static moe.lyrebird.lang.PathUtils.getPathForResource;

/**
 * Created by Tristan on 27/03/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class JsonResourceBundle extends ResourceBundle {
    private static final Path TRANSLATIONS_PATH = getPathForResource("translations").get();
    private static final Path EN_TRANSLATION_PATH = getPathForResource("translations/english.json").get();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private final Map<String, String> properties;
    
    @Override
    public String handleGetObject(@NotNull final String key) {
        return this.properties.get(key);
    }
    
    @NotNull
    @Override
    public Enumeration<String> getKeys() {
        final Iterator<String> keys = this.properties.keySet().iterator();
        return EnumerationUtils.enumerationOf(keys::hasNext, keys::next);
    }
    
    public static JsonResourceBundle getJsonBundle(final String fileName, final Locale locale) {
        final Path translationPath = getPathForBundle(fileName, locale);
        final Map<String, Map<String, String>> translation = loadTranslation(translationPath);
        
        final Map<String, String> translationDotted = new ConcurrentHashMap<>();
        
        translation.forEach((viewName, viewMap) -> {
            viewMap.forEach((key, value) -> {
                translationDotted.put(viewName + '.' + key, value);
            });
        });
        
        return new JsonResourceBundle(translationDotted);
    }
    
    private static Map<String, Map<String, String>> loadTranslation(final Path path) {
        final Try<Map<String, Map<String, String>>> translation = Try.of(() ->
                MAPPER.readValue(
                        path.toFile(),
                        new TypeReference<Map<String, Map<String, String>>>() {
                        }
                ));
        
        return translation.getOrElseGet(cause -> {
            if (path.equals(EN_TRANSLATION_PATH)) {
                throw new RuntimeException("Could not load translations!", cause);
            }
            log.error("Could not load translation file {}", path.toAbsolutePath().toString(), cause);
            return loadTranslation(EN_TRANSLATION_PATH);
        });
    }
    
    private static Path getPathForBundle(final String resourceBundleName, final Locale locale) {
        final String fileName = resourceBundleName + '_' + locale.toString() + ".json";
    
        return Try.of(() -> TRANSLATIONS_PATH.resolve(fileName)).getOrElseGet(cause -> {
            log.error(
                    "Could not find translation at path : {}/{}, defaulting to english",
                    TRANSLATIONS_PATH.toAbsolutePath().toString(),
                    fileName
            );
            return EN_TRANSLATION_PATH;
        });
    }
}
