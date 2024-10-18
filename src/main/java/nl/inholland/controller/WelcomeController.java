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

public class WelcomeController implements Initializable {
    private final User currentUser;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label loggedUserLabel;
    @FXML
    private Label currentDateLabel;

    public WelcomeController(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateLabels();
    }

    // Method to update labels with user data and current date
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

    private String capitalizeString(String inputString) {
        String upperCase = inputString.substring(0, 1).toUpperCase();
        String lowerCase = inputString.substring(1).toLowerCase();

        return upperCase + lowerCase;
    }
}