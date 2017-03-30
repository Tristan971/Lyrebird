package moe.lyrebird.lang;

import lombok.experimental.UtilityClass;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is because Brian Goetz doesn't like you reading files from the classpath
 */
@UtilityClass
public final class PathUtils {
    /**
     * This method gets the {@link Path} associated to a classpath-located file.
     *
     * @param resourceName
     *         The path from the root of the classpath
     *         (where resources are located, and used for them, thus the name)
     * @return The path associated with resource at said relative path to classpath.
     */
    public static Path getPathForResource(final String resourceName) {
        final URL resURL = PathUtils.class.getClassLoader().getResource(resourceName);
        URI resURI = null;
        try {
            assert resURL != null :
                    "Could not access file " + resourceName;
            resURI = resURL.toURI();
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        assert resURI != null :
                "Maven probably didn't copy contents of src/main/resources into target folder. But idk why.";
        return Paths.get(resURI);
    }
}
