package moe.lyrebird.api.server.model.objects;

import java.net.URL;

public class LyrebirdPackage {

    private final TargetPlatform targetPlatform;
    private final URL packageUrl;

    public LyrebirdPackage(TargetPlatform targetPlatform, URL packageUrl) {
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
