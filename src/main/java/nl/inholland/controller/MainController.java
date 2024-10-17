package nl.inholland.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import nl.inholland.model.AccessLevel;
import nl.inholland.model.User;

import java.io.IOException;
import java.util.Objects;

public class MainController {
    @FXML
    private Menu sellMenu;
    @FXML
    private Menu showingsMenu;
    @FXML
    private Menu historyMenu;
    @FXML
    private Label appNameLbl;

    private User currentUser;
    private Menu clickedMenu;

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

    protected void loadScene(String fxmlName, Object controller, String cssName, Menu clickedMenu) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm());

            //if (layout.getChildren().size() > 1)
            //    layout.getChildren().remove(1);
            //layout.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSellMenuClick() {
        //onMenuClick("sell-view.fxml", "/nl/inholland/view/css/sell-view.css", sellMenu);
    }

    @FXML
    protected void onShowingsMenuClick() {
        //onMenuClick("showings-view.fxml", "/nl/inholland/view/css/showings-view.css", showingsMenu);
    }

    @FXML
    protected void onHistoryMenuClick() {
        //onMenuClick("history-view.fxml", "/nl/inholland/view/css/history-view.css", historyMenu);
    }
}
