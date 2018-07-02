package moe.lyrebird.api.server.model.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.List;

public class LyrebirdVersion {

    private final String version;
    private final String buildVersion;
    private final URL changenotesUrl;
    private final List<LyrebirdPackage> packages;

    @JsonCreator
    public LyrebirdVersion(
            @JsonProperty("version") final String version,
            @JsonProperty("buildVersion") final String buildVersion,
            @JsonProperty("changenotesUrl") final URL changenotesUrl,
            @JsonProperty("packages") final List<LyrebirdPackage> packages
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

    @Override
    public String toString() {
        return "LyrebirdVersion{" +
               "version='" + version + '\'' +
               ", buildVersion='" + buildVersion + '\'' +
               ", changenotesUrl=" + changenotesUrl +
               ", packages=" + packages +
               '}';
    }
}
