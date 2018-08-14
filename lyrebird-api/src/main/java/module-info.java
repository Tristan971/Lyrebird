module moe.lyrebird.api {
    exports moe.lyrebird.api.conf;
    exports moe.lyrebird.api.model;
    exports moe.lyrebird.api.client to lyrebird;

    opens moe.lyrebird.api.model to com.fasterxml.jackson.databind;

    requires slf4j.api;

    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;

    requires com.fasterxml.jackson.databind;
    requires jackson.annotations;
    requires java.sql;

}
