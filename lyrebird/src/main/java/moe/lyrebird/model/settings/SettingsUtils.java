package moe.lyrebird.model.settings;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages persistent user application settings.
 *
 * It is mostly a verbose decorator around {@link Preferences#userRoot()}.
 */
public final class SettingsUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsUtils.class);

    private static final Preferences USER_PREFERENCES = Preferences.userRoot().node("Lyrebird");

    private SettingsUtils() {
    }

    public static String get(final Setting setting, final String defaultValue) {
        final String value = USER_PREFERENCES.get(setting.getPreferenceKey(), defaultValue);
        LOG.debug("Fetched user setting {} : {}", setting, value);
        return value;
    }

    public static void set(final Setting setting, final String value) {
        LOG.debug("Saving user setting {} with value {}", setting, value);
        USER_PREFERENCES.put(setting.getPreferenceKey(), value);
    }

    public static boolean get(final Setting setting, final boolean defaultValue) {
        final boolean value = USER_PREFERENCES.getBoolean(setting.getPreferenceKey(), defaultValue);
        LOG.debug("Fetched boolean user setting {} : {}", setting, value);
        return value;
    }

    public static void set(final Setting setting, final boolean value) {
        LOG.debug("Saving boolean user setting {} with value {}", setting, value);
        USER_PREFERENCES.putBoolean(setting.getPreferenceKey(), value);
    }

}
