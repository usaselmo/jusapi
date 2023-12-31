open module jusapi.ui {
    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires spring.jcl;
    requires spring.context;
    requires javax.inject;

    requires jusapi.core;

    requires jusapi.authentication;
    requires spring.security.oauth2.core;
    requires spring.web;
    requires org.apache.tomcat.embed.core;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.core;
    requires spring.beans;
    requires spring.security.config;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.security.oauth2.client;
}