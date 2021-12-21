module ru.ifmo.email.server {
    requires ru.ifmo.email.communication;
    requires ru.ifmo.email.model;
    requires java.sql;
    requires java.desktop;
    requires c3p0;
    requires java.naming;

    exports ru.ifmo.email.server;
    opens ru.ifmo.email.server;
}