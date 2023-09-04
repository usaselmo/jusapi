open module jusapi.core {
    requires kotlin.stdlib;
   requires javax.inject;

   exports core.api.event;
    exports core.api.model;
    exports core.api.email;
}