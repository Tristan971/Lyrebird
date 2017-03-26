package moe.lyrebird.model.io;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is because Brian Goetz doesn't like you reading files from the classpath
 */
@Slf4j
@UtilityClass
public final class ClasspathPathLoader {
    /**
     * This method gets the {@link Path} associated to a classpath-located file.
     *
     * @param resourceName
     *         The path from the root of the classpath
     *         (where resources are located, and used for them, thus the name)
     * @return The path associated with resource at said relative path to classpath.
     */
    public static Path getPathForResource(final String resourceName) {
        final URL resURL = ClasspathPathLoader.class.getClassLoader().getResource(resourceName);
        try {
            assert resURL != null;
            final URI resURI = resURL.toURI();
            return Paths.get(resURI);
        } catch (final Exception e) {
            log.info("Could not load classpath file {}", resourceName, e);
            return null;
        }
    }
}
