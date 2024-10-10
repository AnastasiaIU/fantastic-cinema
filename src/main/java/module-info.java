module org.example.UI {
    requires javafx.fxml;
    requires com.jfoenix;
    requires javafx.controls;
    requires org.burningwave.core;


    opens org.example.UI to javafx.fxml;
    opens org.example.Model to javafx.base;
    exports org.example.UI;
}