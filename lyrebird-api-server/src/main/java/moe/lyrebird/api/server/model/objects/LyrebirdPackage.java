package moe.lyrebird.api.server.model.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public final class LyrebirdPackage {

    private final TargetPlatform targetPlatform;
    private final URL packageUrl;

    @JsonCreator
    public LyrebirdPackage(
            @JsonProperty("targetPlatform") final TargetPlatform targetPlatform,
            @JsonProperty("packageUrl") final URL packageUrl
    ) {
        this.targetPlatform = targetPlatform;
        this.packageUrl = packageUrl;
    }

    public TargetPlatform getTargetPlatform() {
        return targetPlatform;
    }

    public URL getPackageUrl() {
        return packageUrl;
    }

}
