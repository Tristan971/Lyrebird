open module lyrebird.api {
    exports moe.lyrebird.api.model;
    exports moe.lyrebird.api.conf;

    requires slf4j.api;

    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires spring.boot.autoconfigure;

    requires com.fasterxml.jackson.databind;
    requires jackson.annotations;
    requires java.annotation;
    requires java.sql;

    requires org.immutables.value;
    requires immutables.styles;

}
