open module authentication {
    requires kotlin.stdlib;
    requires model;
    requires spring.context;

    exports authentication.api;
}