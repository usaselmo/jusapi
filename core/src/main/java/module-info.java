open module jusapi.core {
    requires kotlin.stdlib;
    requires spring.jcl;
    requires spring.context;

    exports core.api.event;
    exports core.api.model;
    exports core.api.email;
}