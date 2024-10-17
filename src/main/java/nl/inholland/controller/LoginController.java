package nl.inholland.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.inholland.Database;
import nl.inholland.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The LoginController class is responsible for handling the login functionality of the application.
 * It manages user input fields, validates user credentials, and navigates to the main view upon a successful login.
 * The controller uses the JavaFX framework and implements the Initializable interface to set up event handlers
 * when the login screen is initialized.
 */
public class LoginController implements Initializable {
    // Reference to the shared Database instance
    private final Database database;

    // FXML-injected components for the login screen
    @FXML
    private TextField usernameTextField; // Text field for the username input
    @FXML
    private PasswordField passwordField; // Password field for the password input
    @FXML
    private Button loginButton; // Button to trigger the login attempt
    @FXML
    private Label promptLabel; // Label to display login prompts or errors

    /**
     * Constructor that initializes the LoginController with a shared Database instance.
     *
     * @param database The database instance containing user information.
     */
    public LoginController(Database database) {
        this.database = database;
    }

    /**
     * Called automatically after the FXML elements have been loaded.
     * Initializes the login button with an event handler that validates user credentials
     * and navigates to the welcome screen upon successful login.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if not available.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(event -> {
            promptLabel.setVisible(false);

            String username = usernameTextField.getText();
            String password = passwordField.getText();

            User user = login(username, password);

            showMainView(user);
        });
    }

    /**
     * Validates user credentials.
     *
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @return The User object if credentials are valid, otherwise null.
     */
    private User login(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Find a user by username.
     *
     * @param username The username to search for.
     * @return The User object if found, otherwise null.
     */
    private User findUserByUsername(String username) {
        for (User user : database.getUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Displays the main view of the application if the login is successful.
     * If the user is authenticated, the main view is loaded, and the user's information
     * is passed to the next controller. If authentication fails, a prompt is shown to the user.
     *
     * @param user The authenticated user. If null, the login is considered unsuccessful.
     */
    private void showMainView(User user) {
        if (user != null) {
            // Successful login, open main-view.fxml
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/main-view.fxml"));
                fxmlLoader.setController(new MainController(database, user));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/nl/inholland/view/css/main-view.css")).toExternalForm());
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Invalid login
            promptLabel.setVisible(true);
        }
    }
}
