package moe.lyrebird.lang;

import javaslang.control.Try;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class is because Brian Goetz doesn't like you reading files from the classpath
 */
@Slf4j
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
    public static Try<Path> getPathForResource(final String resourceName) {
        return Try.of(() -> {
            final URL resURL = PathUtils.class.getClassLoader().getResource(resourceName);
            assert resURL != null;
            final URI resURI = resURL.toURI();
            return Paths.get(resURI);
        });
    }
    
    /**
     * Returns a stream from the files in the given directory.
     * Simple wrapper around {@link DirectoryStream}.
     *
     * @param directory
     *         The directory to iterate over
     * @return A stream of the files under the given directory or an empty stream if
     * the {@link Path} was not a directory.
     */
    public static Stream<Path> filesInDirectory(final Path directory) {
        try {
            final DirectoryStream<Path> files = Files.newDirectoryStream(directory);
            return StreamSupport.stream(files.spliterator(), false);
        } catch (final IOException e) {
            log.error(
                    "Could not access files at location {}",
                    directory.toAbsolutePath().toString(),
                    e
            );
            return Stream.empty();
        }
    }
}
