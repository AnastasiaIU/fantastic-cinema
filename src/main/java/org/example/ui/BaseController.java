package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.example.model.AccessLevel;
import org.example.model.User;

import java.io.IOException;

public class BaseController implements InitializableMenu {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu sellMenu;
    @FXML
    protected Menu showingsMenu;
    @FXML
    private Menu historyMenu;

    protected User currentUser;

    @Override
    public void initialize(User currentUser, Menu clickedMenu) {
        this.currentUser = currentUser;
        setMenu(clickedMenu);
    }

    protected void setMenu(Menu clickedMenu) {
        // Set the menu based on the user's access level
        if (currentUser.getAccessLevel() == AccessLevel.MANAGEMENT) {
            sellMenu.setDisable(true);
        } else {
            showingsMenu.setDisable(true);
            historyMenu.setDisable(true);
        }

        if (clickedMenu != null) {
            switch (clickedMenu.getId()) {
                case "sellMenu":
                    sellMenu.setDisable(true);
                    break;
                case "showingsMenu":
                    showingsMenu.setDisable(true);
                    break;
                case "historyMenu":
                    historyMenu.setDisable(true);
                    break;
            }
        }
    }

    protected void onMenuClick(String fxmlName, String cssName, Menu clickedMenu) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage)menuBar.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(cssName).toExternalForm());

            InitializableMenu controller = fxmlLoader.getController();
            controller.initialize(currentUser, clickedMenu);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSellMenuClick() {
        onMenuClick("sell-view.fxml", "/org/example/css/sell-view.css", sellMenu);
    }

    @FXML
    protected void onShowingsMenuClick() {
        onMenuClick("showings-view.fxml", "/org/example/css/showings-view.css", showingsMenu);
    }

    @FXML
    protected void onHistoryMenuClick() {
        onMenuClick("history-view.fxml", "/org/example/css/history-view.css", historyMenu);
    }
}
