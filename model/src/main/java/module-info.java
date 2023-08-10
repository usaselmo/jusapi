open module model {
    requires kotlin.stdlib;
    requires spring.jcl;
    requires spring.context;

    exports model.api.event;
    exports model.api;
}