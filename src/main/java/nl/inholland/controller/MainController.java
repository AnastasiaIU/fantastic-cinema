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

/**
 * Controller for the main view of the application.
 * This class manages the main menu and its navigation, controlling the visibility and access levels for different
 * menu options based on the current user's access level. It implements the {@link Initializable} interface to set up
 * the view when the controller is loaded.
 */
public class MainController implements Initializable {
    // Pseudo-classes used to style the active and inactive menu buttons
    private final PseudoClass ACTIVE_CLASS = PseudoClass.getPseudoClass("active");
    private final PseudoClass INACTIVE_CLASS = PseudoClass.getPseudoClass("inactive");

    // Reference to the shared Database instance
    private final Database database;
    // Reference to the currently logged-in user
    private final User currentUser;

    // FXML-injected components
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

    /**
     * Constructor for the MainController.
     *
     * @param database    The database instance shared across controllers, used to access application data.
     * @param currentUser The database instance shared across controllers.
     */
    public MainController(Database database, User currentUser) {
        this.database = database;
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller and sets up the initial state of the menu and its listeners.
     * This method is called automatically after the FXML file is loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMenuBasedOnAccess();
        setMenuBasedOnClick(null);
        setListenersToMenu();
        loadScene("/nl/inholland/view/welcome-view.fxml", new WelcomeController(currentUser));
    }

    /**
     * Loads a new scene into the main view based on the provided FXML file and controller.
     *
     * @param fxmlName   The name of the FXML file to load.
     * @param controller The controller instance associated with the new view.
     */
    private void loadScene(String fxmlName, Object controller) {
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

    /**
     * Sets the menu options based on the user's access level.
     * Disables certain buttons if the user does not have the required access level (e.g., MANAGEMENT or SALES).
     */
    private void setMenuBasedOnAccess() {
        // Set the menu based on the user's access level
        if (currentUser.getAccessLevel() == AccessLevel.MANAGEMENT) {
            Objects.requireNonNull(sellMenuButton).setDisable(true);
        } else {
            Objects.requireNonNull(showingsMenuButton).setDisable(true);
            Objects.requireNonNull(historyMenuButton).setDisable(true);
        }
    }

    /**
     * Disables the clicked menu button and sets other buttons as inactive.
     * This method ensures that the clicked menu button cannot be clicked again and updates the styling accordingly.
     *
     * @param clickedMenu The button that was clicked by the user.
     */
    private void setMenuBasedOnClick(Button clickedMenu) {
        for (Node node : header.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(node == clickedMenu);
                node.pseudoClassStateChanged(ACTIVE_CLASS, node == clickedMenu);
                node.pseudoClassStateChanged(INACTIVE_CLASS, node != clickedMenu);
            }
        }

        setMenuBasedOnAccess();
    }

    /**
     * Adds event listeners to the menu buttons to handle scene loading when they are clicked.
     * Each button click triggers the loading of the corresponding view and updates the menu based on the clicked button.
     */
    private void setListenersToMenu() {
        sellMenuButton.setOnAction(event -> {
            loadScene("/nl/inholland/view/sell-view.fxml", new SellController(database, root));
            setMenuBasedOnClick(sellMenuButton);
        });

        showingsMenuButton.setOnAction(event -> {
            loadScene("/nl/inholland/view/showings-view.fxml", new ShowingsController(database, root));
            setMenuBasedOnClick(showingsMenuButton);
        });

        historyMenuButton.setOnAction(event -> {
            loadScene("/nl/inholland/view/history-view.fxml", new HistoryController(database));
            setMenuBasedOnClick(historyMenuButton);
        });
    }
}
