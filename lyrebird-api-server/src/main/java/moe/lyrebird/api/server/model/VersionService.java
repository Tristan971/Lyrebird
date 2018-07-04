package moe.lyrebird.api.server.model;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyrebird.api.server.model.objects.LyrebirdVersion;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.unchecked;

@Component
public class VersionService {

    private static final List<LyrebirdVersion> VERSIONS = findAllVersions();

    public LyrebirdVersion getLatestVersion() {
        return VERSIONS.get(0);
    }

    private static List<LyrebirdVersion> findAllVersions() {
        final PathMatchingResourcePatternResolver versionsResourcesResolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] versionResources = versionsResourcesResolver.getResources("classpath:versions/*");
            final ObjectMapper mapper = new ObjectMapper();
            return Arrays.stream(versionResources)
                         .map(unchecked(Resource::getInputStream))
                         .map(unchecked(is -> mapper.readValue(is, LyrebirdVersion.class)))
                         .sorted(Comparator.comparing(LyrebirdVersion::getBuildVersion))
                         .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
