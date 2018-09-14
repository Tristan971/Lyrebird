module lyrebird.server {

    opens moe.lyrebird.api.server to spring.core;

    exports moe.lyrebird.api.server to spring.beans, spring.context;
    exports moe.lyrebird.api.server.model to spring.beans;
    exports moe.lyrebird.api.server.controllers to spring.beans, spring.web;

    requires slf4j.api;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.security.config;
    requires spring.web;

    requires com.fasterxml.jackson.databind;

    requires lyrebird.api;
    requires io.vavr;

}
