package nl.inholland.controller;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nl.inholland.Database;
import nl.inholland.model.AccessLevel;
import nl.inholland.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // Reference to the shared Database instance
    private final Database database;

    private final User currentUser;

    private final PseudoClass activeClass = PseudoClass.getPseudoClass("active");
    private final PseudoClass inactiveClass = PseudoClass.getPseudoClass("inactive");

    @FXML
    private VBox root;
    @FXML
    private Button sellMenuButton;
    @FXML
    private Button showingsMenuButton;
    @FXML
    private Button historyMenuButton;
    @FXML
    private HBox header;

    public MainController(Database database, User currentUser) {
        this.database = database;
        this.currentUser = currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuBasedOnAccess();
        setMenuBasedOnClick(null);

        sellMenuButton.setOnAction(event -> {
            loadScene("/nl/inholland/view/sell-view.fxml", new SellController(database, root));
            setMenuBasedOnClick(sellMenuButton);
        });

        showingsMenuButton.setOnAction(event -> {
            //loadScene("/nl/inholland/view/showings-view.fxml", "/nl/inholland/view/css/showings-view.css", new ShowingsController());
            setMenuBasedOnClick(showingsMenuButton);
        });

        historyMenuButton.setOnAction(event -> {
            //loadScene("/nl/inholland/view/history-view.fxml", "/nl/inholland/view/css/history-view.css", new HistoryController());
            setMenuBasedOnClick(historyMenuButton);
        });

        loadScene("/nl/inholland/view/welcome-view.fxml", new WelcomeController(currentUser));
    }

    protected void loadScene(String fxmlName, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setMenuBasedOnAccess() {
        // Set the menu based on the user's access level
        if (currentUser.getAccessLevel() == AccessLevel.MANAGEMENT) {
            Objects.requireNonNull(sellMenuButton).setDisable(true);
        } else {
            Objects.requireNonNull(showingsMenuButton).setDisable(true);
            Objects.requireNonNull(historyMenuButton).setDisable(true);
        }
    }

    protected void setMenuBasedOnClick(Button clickedMenu) {
        for (Node node : header.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(node == clickedMenu);
                node.pseudoClassStateChanged(activeClass, node == clickedMenu);
                node.pseudoClassStateChanged(inactiveClass, node != clickedMenu);
            }
        }

        setMenuBasedOnAccess();
    }
}
