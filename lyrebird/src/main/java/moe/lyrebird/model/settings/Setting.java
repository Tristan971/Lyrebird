package moe.lyrebird.model.settings;

public enum Setting {
    NOTIFICATION_MAIN_STAGE_TRAY_SEEN("ui.notification.mainStageCloseToTray");

    private final String preferenceKey;

    Setting(final String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

}
