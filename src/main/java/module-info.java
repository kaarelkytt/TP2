module org.example.tp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.tp to javafx.fxml;
    exports org.example.tp;
}