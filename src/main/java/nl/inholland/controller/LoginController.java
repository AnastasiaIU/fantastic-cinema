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
 * Controller for the login view in the application.
 * This class manages the login process, verifies user credentials, and navigates to the main view upon successful login.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class LoginController implements Initializable {
    // Reference to the shared Database instance
    private final Database database;

    // FXML-injected components
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label promptLabel;

    /**
     * Constructor for the LoginController.
     *
     * @param database The database instance shared across controllers.
     */
    public LoginController(Database database) {
        this.database = database;
    }

    /**
     * Initializes the controller and sets up the login action when the login button is pressed or when the Enter key
     * is pressed within the username or password fields.
     * This method is called automatically after the FXML file is loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(event -> {
            attemptLogin();
        });

        usernameTextField.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                attemptLogin();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to log in a user based on the input provided in the username and password fields.
     * This method retrieves the username and password entered by the user, validates them by calling
     * the {@link #login(String, String)} method, and proceeds to show the main view if the credentials are correct.
     * If the credentials are invalid, the prompt label is set visible to indicate an error.
     * This method is intended to be reused when the login button is pressed or when the Enter key
     * is pressed within the username or password fields.
     */
    private void attemptLogin() {
        promptLabel.setVisible(false);

        String username = usernameTextField.getText();
        String password = passwordField.getText();

        User user = login(username, password);

        showMainView(user);
    }

    /**
     * Validates user credentials by checking if the username and password match a user in the database.
     *
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @return The {@link User} object if the credentials are valid, otherwise null.
     */
    private User login(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Searches for a user by username in the database.
     *
     * @param username The username to search for.
     * @return The {@link User} object if found, otherwise null.
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
     * Navigates to the main view upon successful login or displays an error if the login fails.
     *
     * @param user The {@link User} object of the logged-in user. If null, login failed.
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
