package moe.lyrebird.model.update.selfupdate.linux;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PackageKitService {

    private static final Logger LOG = LoggerFactory.getLogger(PackageKitService.class);

    public static void showNativeUnixPackageKitPrompt() {
        LOG.info("Calling native unix update prompt provided by PackageKit over DBus");

        try {
            final DBusConnection conn = DBusConnection.getConnection(DBusConnection.SESSION);
            LOG.debug("Got DBus connection : {}", conn);

            final IPackageKit packageKit = conn.getRemoteObject(
                    "org.freedesktop.PackageKit",
                    "/org/freedesktop/PackageKit",
                    IPackageKit.class
            );
            LOG.debug("Got PackageKit interface binding : {}", packageKit);

            final String displayName = System.getenv("DISPLAY");
            final long displayNumber = displayName == null ? 0L : Integer.parseInt(displayName.substring(1));
            LOG.debug("Will display update prompt on screen {} [from $DISPLAY = {}]", displayNumber, displayName);

            packageKit.InstallPackageNames(new UInt32(displayNumber), new String[] {"lyrebird"}, "show-confirm-search,hide-finished");
            LOG.debug("Successfully invoked PackageKit InstallPackageNames method.");
        } catch (DBusException e) {
            LOG.error("Could not invoke PackageKit", e);
        }

    }

    /**
     * This class represents a subset of the PackageKit Session DBus interface that's relevant to us.
     *
     * <a href="https://blog.fpmurphy.com/2013/11/packagekit-d-bus-abstraction-layer.html">PackageKit DBus abstraction layer</a>
     * <a href="https://techbase.kde.org/Development/Tutorials/PackageKit_Session_Interface">PackageKit Session Interface</a>
     *
     * @author Lourkeur
     */
    @DBusInterfaceName("org.freedesktop.PackageKit.Transaction")
    interface IPackageKit extends DBusInterface {

        void InstallPackageNames(UInt32 xid, String[] packages, String interaction);

    }

}
