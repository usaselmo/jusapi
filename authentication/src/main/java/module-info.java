open module authentication {
    requires kotlin.stdlib;
    requires model;
    requires spring.context;
    requires spring.jcl;

    exports authentication.api;
}