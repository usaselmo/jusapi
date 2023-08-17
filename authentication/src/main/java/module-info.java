open module jusapi.authentication {
    requires kotlin.stdlib;
    requires jusapi.core;
    requires spring.context;
    requires spring.jcl;

    exports authentication.api;
}