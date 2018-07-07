package moe.lyrebird.model.twitter.util;

import java.util.Arrays;

public enum MediaEntityType {
    PHOTO("photo"),
    UNMANAGED("<UNMANAGED_TYPE>");

    private final String codeName;

    MediaEntityType(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

    public static MediaEntityType fromTwitterType(final String actualCode) {
        return Arrays.stream(values())
                .filter(type -> type.getCodeName().equals(actualCode))
                .findAny()
                .orElse(UNMANAGED);
    }

}
