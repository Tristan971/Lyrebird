package moe.lyrebird.model.update.selfupdate.linux;

import java.util.concurrent.atomic.AtomicReference;

import org.freedesktop.dbus.CallbackHandler;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PackageKitService {

    private static final Logger LOG = LoggerFactory.getLogger(PackageKitService.class);

    private AtomicReference<DBusConnection> dbusConnectionHolder = new AtomicReference<>();

    private DBusConnection getDbusConnectionHolder() {
        if (dbusConnectionHolder.getAcquire() == null) {
            try {
                dbusConnectionHolder.setRelease(DBusConnection.getConnection(DBusConnection.SESSION));
            } catch (DBusException e) {
                LOG.error("Could not acquire connection to the DBus messaging system!", e);
            }
        }
        return dbusConnectionHolder.get();
    }

    public void showNativeUnixPackageKitPrompt() {
        LOG.info("Calling native unix update prompt provided by PackageKit over DBus");

        final DBusConnection conn = getDbusConnectionHolder();
        LOG.debug("Got DBus connection : {}", conn);
        try {
            final IPackageKit packageKit = conn.getRemoteObject(
                    "org.freedesktop.PackageKit",
                    "/org/freedesktop/PackageKit",
                    IPackageKit.class
            );
            LOG.debug("Got PackageKit interface binding : {}", packageKit);

            final String displayName = System.getenv("DISPLAY");
            final long displayNumber = displayName == null ? 0L : Integer.parseInt(displayName.substring(1));
            LOG.debug("Will display update prompt on screen {} [from $DISPLAY = {}]", displayNumber, displayName);

            packageKit.InstallPackageNames(new UInt32(displayNumber), new String[] {"lyrebird"}, "");
            LOG.debug("Successfully invoked PackageKit InstallPackageNames method.");
        } catch (DBusException e) {
            LOG.error("Could not invoke PackageKit", e);
        }

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
@DBusInterfaceName("org.freedesktop.PackageKit.Modify")
interface IPackageKit extends DBusInterface {

    void InstallPackageNames(UInt32 xid, String[] packages, String interaction);

}
