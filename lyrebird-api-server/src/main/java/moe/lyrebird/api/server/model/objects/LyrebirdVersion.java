package moe.lyrebird.api.server.model.objects;

import java.net.URL;
import java.util.List;

public class LyrebirdVersion {

    private final String version;
    private final String buildVersion;
    private final URL changenotesUrl;
    private final List<LyrebirdPackage> packages;

    public LyrebirdVersion(
            String version,
            String buildVersion,
            URL changenotesUrl,
            List<LyrebirdPackage> packages
    ) {
        this.version = version;
        this.buildVersion = buildVersion;
        this.changenotesUrl = changenotesUrl;
        this.packages = packages;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public URL getChangenotesUrl() {
        return changenotesUrl;
    }

    public List<LyrebirdPackage> getPackages() {
        return packages;
    }

}
