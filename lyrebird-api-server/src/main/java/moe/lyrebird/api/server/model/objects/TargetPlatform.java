package moe.lyrebird.api.server.model.objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TargetPlatform {

    WINDOWS("win", "Windows"),
    MACOS("mac", "macOS"),
    LINUX_DEB("deb", "Debian & derivatives (Ubuntu...)"),
    LINUX_RPM("rpm", "RPM-based distributions"),
    UNIVERSAL_JAVA("java", "Universal distribution (JDK required)");

    private final String codename;
    private final String readableName;

    TargetPlatform(String codename, String readableName) {
        this.codename = codename;
        this.readableName = readableName;
    }

    @JsonCreator
    public static TargetPlatform fromCodename(final String codename) {
        return Arrays.stream(values())
                     .filter(v -> v.codename.equals(codename))
                     .findAny()
                     .orElse(UNIVERSAL_JAVA);
    }

}
