module lyrebird {
    requires spring.context;
    requires easyfxml;
    requires slf4j.api;
    requires twitter4j.core;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafxsvg;
    requires spring.beans;
    requires spring.core;
    requires twitter4j.stream;
    requires io.vavr;
    requires spring.data.jpa;
    requires java.persistence;
    requires spring.boot.autoconfigure;

    exports moe.lyrebird to javafx.graphics;
}
