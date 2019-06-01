open module lyrebird.server {

    exports moe.lyrebird.api.server;
    exports moe.lyrebird.api.server.model;
    exports moe.lyrebird.api.server.controllers;

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
