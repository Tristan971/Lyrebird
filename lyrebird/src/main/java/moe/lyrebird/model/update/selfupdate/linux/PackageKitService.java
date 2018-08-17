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
        final DBusConnection conn = getDbusConnectionHolder();
        try {
            final IPackageKit packageKit = conn.getRemoteObject(
                    "org.freedesktop.PackageKit",
                    "/org/freedesktop/PackageKit",
                    IPackageKit.class
            );
            // FIXME: find the correct XID
            packageKit.InstallPackageNames(new UInt32(0), new String[] {"lyrebird"}, "");
        } catch (DBusException e) {
            e.printStackTrace();
        }

    }

    private static final class DBusCallbackHandler implements CallbackHandler<Object> {

        @Override
        public void handle(Object r) {
            LOG.info("Received dbus message back : {}", r);
        }

        @Override
        public void handleError(DBusExecutionException e) {
            LOG.error("Received dbus error back : {}", e);
        }

    }

}

/* subset of the PackageKit Session DBus interface that's relevant to us
 *
 * https://blog.fpmurphy.com/2013/11/packagekit-d-bus-abstraction-layer.html
 * https://techbase.kde.org/Development/Tutorials/PackageKit_Session_Interface
 */
@DBusInterfaceName("org.freedesktop.PackageKit.Modify")
interface IPackageKit extends DBusInterface {
    void InstallPackageNames(UInt32 xid, String[] packages, String interaction);
}
