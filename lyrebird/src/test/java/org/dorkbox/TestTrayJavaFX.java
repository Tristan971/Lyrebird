/*
 * Copyright 2015 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dorkbox;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import dorkbox.systemTray.Checkbox;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.CacheUtil;
import dorkbox.util.Desktop;
import dorkbox.util.JavaFX;
import dorkbox.util.OS;

/**
 * Icons from 'SJJB Icons', public domain/CC0 icon set
 * <p>
 * Needs JavaFX to run. NOTE: JavaFX on Mac (Java7) has many bugs when also used with AWT. This class does NOT extend 'Application'
 * (javafx) class on purpose, so that we can work around those issues
 */
@SuppressWarnings("WeakerAccess")
public
class TestTrayJavaFX {

    public static final URL BLUE_CAMPING = TestTray.class.getResource("accommodation_camping.glow.0092DA.32.png");
    public static final URL BLACK_FIRE = TestTray.class.getResource("amenity_firestation.p.000000.32.png");

    public static final URL BLACK_MAIL = TestTray.class.getResource("amenity_post_box.p.000000.32.png");
    public static final URL GREEN_MAIL = TestTray.class.getResource("amenity_post_box.p.39AC39.32.png");

    public static final URL BLACK_BUS = TestTray.class.getResource("transport_bus_station.p.000000.32.png");
    public static final URL LT_GRAY_BUS = TestTray.class.getResource("transport_bus_station.p.999999.32.png");

    public static final URL BLACK_TRAIN = TestTray.class.getResource("transport_train_station.p.000000.32.png");
    public static final URL GREEN_TRAIN = TestTray.class.getResource("transport_train_station.p.39AC39.32.png");
    public static final URL LT_GRAY_TRAIN = TestTray.class.getResource("transport_train_station.p.666666.32.png");

    private static TestTrayJavaFX testTrayJavaFX;

    static {
        SystemTray.FORCE_GTK2 = true;
    }

    public static
    class MyApplication extends Application {

        public MyApplication() {
        }

        @Override
        public void start(final Stage stage) {
            if (testTrayJavaFX == null) {
                testTrayJavaFX = new TestTrayJavaFX();
            }

            testTrayJavaFX.doJavaFxStuff(stage);
        }

    }

    public static void main(String[] args) {
        if (OS.isMacOsX() && OS.javaVersion <= 7) {
            System.setProperty("javafx.macosx.embedded", "true");
            java.awt.Toolkit.getDefaultToolkit();
        }

        testTrayJavaFX = new TestTrayJavaFX();

        Application application = new MyApplication();

        // make sure JNA jar is on the classpath!
        //noinspection AccessStaticViaInstance
        application.launch(MyApplication.class);
    }

    private SystemTray systemTray;
    private ActionListener callbackGray;

    public TestTrayJavaFX() {

    }

    public void doJavaFxStuff(final Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(event -> System.out.println("Hello World!"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

        SystemTray.DEBUG = true; // for test apps, we always want to run in debug mode
        CacheUtil.clear(); // for test apps, make sure the cache is always reset. You should never do this in production.

        // SwingUtil.setLookAndFeel(null); // set Native L&F (this is the System L&F instead of CrossPlatform L&F)
        // SystemTray.SWING_UI = new CustomSwingUI();

        this.systemTray = SystemTray.get();
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.setTooltip("Mail Checker");
        systemTray.setImage(LT_GRAY_TRAIN);
        systemTray.setStatus("No Mail");

        callbackGray = e -> {
            final MenuItem entry = (MenuItem) e.getSource();
            systemTray.setStatus(null);
            systemTray.setImage(BLACK_TRAIN);

            entry.setCallback(null);
//                systemTray.setStatus("Mail Empty");
            systemTray.getMenu().remove(entry);
            System.err.println("POW");
        };


        Menu mainMenu = systemTray.getMenu();

        MenuItem greenEntry = new MenuItem("Green Mail", e -> {
            final MenuItem entry = (MenuItem) e.getSource();
            systemTray.setStatus("Some Mail!");
            systemTray.setImage(GREEN_TRAIN);

            entry.setCallback(callbackGray);
            entry.setImage(BLACK_MAIL);
            entry.setText("Delete Mail");
            entry.setTooltip(null); // remove the tooltip
//                systemTray.remove(menuEntry);
        });
        greenEntry.setImage(GREEN_MAIL);
        // case does not matter
        greenEntry.setShortcut('G');
        greenEntry.setTooltip("This means you have green mail!");
        mainMenu.add(greenEntry);


        Checkbox checkbox = new Checkbox("Euro € Mail", e -> System.err.println("Am i checked? " + ((Checkbox) e.getSource()).getChecked()));
        checkbox.setShortcut('€');
        mainMenu.add(checkbox);

        mainMenu.add(new Separator());

        mainMenu.add(new MenuItem("About", e -> {
            try {
                Desktop.browseURL("https://git.dorkbox.com/dorkbox/SystemTray");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));

        mainMenu.add(new MenuItem("Temp Directory", e -> {
            try {
                Desktop.browseDirectory(OS.TEMP_DIR.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }));

        Menu submenu = new Menu("Options", BLUE_CAMPING);
        submenu.setShortcut('t');


        MenuItem disableMenu = new MenuItem("Disable menu", BLACK_BUS, e -> {
            MenuItem source = (MenuItem) e.getSource();
            source.getParent().setEnabled(false);
        });
        submenu.add(disableMenu);


        submenu.add(new MenuItem("Hide tray", LT_GRAY_BUS, e -> systemTray.setEnabled(false)));
        submenu.add(new MenuItem("Remove menu", BLACK_FIRE, e -> {
            MenuItem source = (MenuItem) e.getSource();
            source.getParent().remove();
        }));
        submenu.add(new MenuItem("Add new entry to tray", e -> systemTray.getMenu().add(new MenuItem("Random " + new Random().nextInt(10)))));
        mainMenu.add(submenu);

        MenuItem entry = new MenuItem("Type: " + systemTray.toString());
        entry.setEnabled(false);
        systemTray.getMenu().add(entry);

        systemTray.getMenu().add(new MenuItem("Quit", e -> {
            systemTray.shutdown();

            if (!JavaFX.isEventThread()) {
                JavaFX.dispatch(() -> {
                    primaryStage.hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
                    Platform.exit();  // necessary to close javaFx
                });
            } else {
                primaryStage.hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
                Platform.exit();  // necessary to close javaFx
            }

            //System.exit(0);  not necessary if all non-daemon threads have stopped.
        })).setShortcut('q'); // case does not matter
    }

}
