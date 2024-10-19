package nl.inholland.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import nl.inholland.model.AccessLevel;
import nl.inholland.model.User;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the welcome view in the application.
 * This class displays a welcome message to the user, their role, and continuously updates the current date and time.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class WelcomeController implements Initializable {
    // Reference to the currently logged-in user
    private final User currentUser;

    // FXML-injected components
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label loggedUserLabel;
    @FXML
    private Label currentDateLabel;

    /**
     * Constructor for the WelcomeController.
     *
     * @param currentUser The user currently logged into the application.
     */
    public WelcomeController(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Initializes the controller and sets up the initial state of the welcome view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateLabels();
    }

    /**
     * Updates the labels with user information and the current date and time.
     * Sets up a {@link Timeline} to refresh the date and time every second.
     */
    private void updateLabels() {
        // Update welcome label
        welcomeLabel.setText("Welcome " + capitalizeString(currentUser.getUsername()));

        // Update logged user label
        String role = (currentUser.getAccessLevel() == AccessLevel.MANAGEMENT) ? "manager" : "sales";
        loggedUserLabel.setText("You are logged in as " + role);

        // DateTimeFormatter for the date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Set the initial date and time to display it without a delay
        String initialFormattedDateTime = LocalDateTime.now().format(formatter);
        currentDateLabel.setText("The current date and time is " + initialFormattedDateTime);

        // Create a Timeline to update the current time every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String formattedDateTime = LocalDateTime.now().format(formatter);
            currentDateLabel.setText("The current date and time is " + formattedDateTime);
        }));

        // Set the timeline to run indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play(); // Start the timeline
    }

    /**
     * Capitalizes the first letter of the input string while making the rest lowercase.
     *
     * @param inputString The input string to be formatted.
     * @return The formatted string with the first character capitalized and the rest in lowercase.
     */
    private String capitalizeString(String inputString) {
        String upperCase = inputString.substring(0, 1).toUpperCase();
        String lowerCase = inputString.substring(1).toLowerCase();

        return upperCase + lowerCase;
    }
}
