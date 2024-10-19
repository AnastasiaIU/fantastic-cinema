package nl.inholland.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import nl.inholland.model.AccessLevel;
import nl.inholland.model.User;

import java.io.IOException;

public abstract class BaseController implements InitializableMenu {
    @FXML
    private Menu sellMenu;
    @FXML
    protected Menu showingsMenu;
    @FXML
    private Menu historyMenu;
    @FXML
    private Label appNameLbl;

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

            Stage stage = (Stage)appNameLbl.getScene().getWindow();
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
        onMenuClick("sell-view.fxml", "/nl/inholland/view/css/sell-view.css", sellMenu);
    }

    @FXML
    protected void onShowingsMenuClick() {
        onMenuClick("showings-view.fxml", "/nl/inholland/view/css/showings-view.css", showingsMenu);
    }

    @FXML
    protected void onHistoryMenuClick() {
        onMenuClick("history-view.fxml", "/nl/inholland/view/css/history-view.css", historyMenu);
    }
}
