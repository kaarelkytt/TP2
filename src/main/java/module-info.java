
module org.example.tp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.sql;

    //requires com.h2database;
    //requires org.hibernate.orm.core;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires com.sun.xml.bind;
    requires java.desktop;
    requires com.google.gson;
    requires com.h2database;

    opens org.example.tp.controllers to javafx.fxml;
    opens org.example.tp.dataobjects to org.hibernate.orm.core, com.google.gson;
    exports org.example.tp;
}