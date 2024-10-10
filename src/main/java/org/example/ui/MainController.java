package org.example.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.util.Duration;
import org.example.model.AccessLevel;
import org.example.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController extends BaseController {
    @FXML
    private Label welcomeLbl;
    @FXML
    private Label loggedUserLbl;
    @FXML
    private Label currentDateLbl;

    @Override
    public void initialize(User currentUser, Menu clickedMenu) {
        this.currentUser = currentUser;
        updateLabels();
        setMenu(clickedMenu);
    }

    // Method to update labels with user data and current date
    private void updateLabels() {
        // Update welcome label
        welcomeLbl.setText("Welcome " + capitalizeString(currentUser.getUsername()));

        // Update logged user label
        String role = (currentUser.getAccessLevel() == AccessLevel.MANAGEMENT) ? "manager" : "sales";
        loggedUserLbl.setText("You are logged in as " + role);

        // DateTimeFormatter for the date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Set the initial date and time to display it without a delay
        String initialFormattedDateTime = LocalDateTime.now().format(formatter);
        currentDateLbl.setText("The current date and time is " + initialFormattedDateTime);

        // Create a Timeline to update the current time every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String formattedDateTime = LocalDateTime.now().format(formatter);
            currentDateLbl.setText("The current date and time is " + formattedDateTime);
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
