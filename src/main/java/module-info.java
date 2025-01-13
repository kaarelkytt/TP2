
module org.example.tp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;

    //requires com.h2database;
    //requires org.hibernate.orm.core;
    requires java.sql;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires com.sun.xml.bind;

    opens org.example.tp.controllers to javafx.fxml;
    opens org.example.tp.dataobjects to org.hibernate.orm.core;
    exports org.example.tp;
}