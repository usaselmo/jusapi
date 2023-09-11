open module jusapi.core {
    requires kotlin.stdlib;
   requires javax.inject;
   requires spring.context;

   exports core.api.event;
    exports core.api.model;
    exports core.api.email;
}