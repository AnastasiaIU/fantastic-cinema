module nl.inholland.view {
    requires javafx.fxml;
    requires javafx.controls;


    opens nl.inholland.controller to javafx.fxml;
    opens nl.inholland.model to javafx.base;
    exports nl.inholland.controller;
    exports nl.inholland;
    exports nl.inholland.model;
    opens nl.inholland to javafx.fxml;
}