module org.example.ui {
    requires javafx.fxml;
    requires javafx.controls;


    opens org.example.ui to javafx.fxml;
    opens org.example.model to javafx.base;
    exports org.example.ui;
    exports org.example;
    opens org.example to javafx.fxml;
}