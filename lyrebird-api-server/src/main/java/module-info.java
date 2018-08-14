module moe.lyrebird.api.server {

    requires slf4j.api;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.security.config;
    requires spring.web;

    requires com.fasterxml.jackson.databind;
    requires io.vavr;

    requires moe.lyrebird.api;

}
