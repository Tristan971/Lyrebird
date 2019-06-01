open module lyrebird {
    requires lyrebird.api;
    requires org.twitter4j.core;

    requires java.persistence;
    requires java.prefs;

    requires moe.tristan.easyfxml;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires SystemTray;

    requires jackson.annotations;
    requires com.fasterxml.jackson.databind;

    requires io.vavr;
    requires net.bytebuddy;
    requires oshi.core;
    requires prettytime;
    requires slf4j.api;

    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.boot;
    requires spring.web;
}
