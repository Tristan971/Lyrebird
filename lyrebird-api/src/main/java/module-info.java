module moe.lyrebird.api {
    exports moe.lyrebird.api.conf;
    exports moe.lyrebird.api.model;

    requires slf4j.api;

    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;

    requires com.fasterxml.jackson.databind;
    requires jackson.annotations;

}
