package nl.inholland.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.inholland.Database;
import nl.inholland.model.Selling;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class AgeCheckController implements Initializable {
    // Formatter for date and time values in dd-MM-yyyy HH:mm format
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Reference to the shared Database instance
    private final Database database;
    // Reference to the currently selected showing
    private final Selling selling;
    private Stage dialogStage;
    private boolean confirmed;

    // FXML-injected components
    @FXML
    private Label titleLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label numberOfTicketsLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private CheckBox ageCheckBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    public AgeCheckController(Database database, Selling selectedShowing) {
        this.database = database;
        this.selling = selectedShowing;
        this.confirmed = false; // Initialize as false by default
    }

    /**
     * Initializes the controller and sets up the initial state of the age check view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText(selling.getShowing().getTitle());
        dateTimeLabel.setText(selling.getShowing().getStartDateTime().format(FORMATTER));
        numberOfTicketsLabel.setText(String.valueOf(selling.getTicketsSold()));
        customerNameLabel.setText(selling.getCustomer());

        addListenerForDisablingConfirmButton();
        addListenersToButtons();
    }

    private void addListenerForDisablingConfirmButton() {
        ageCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    confirmButton.setDisable(!newValue);
                }
        );
    }

    /**
     * Adds action listeners to the confirm and cancel buttons.
     */
    private void addListenersToButtons() {
        confirmButton.setOnAction(event -> {
            confirmed = true; // Set the result as confirmed
            dialogStage.close(); // Close the dialog
        });

        cancelButton.setOnAction(event -> {
            confirmed = false; // Set the result as canceled
            dialogStage.close(); // Close the dialog
        });
    }

    public boolean showDialog(Stage parentStage) {
        try {
            // Load the FXML file for the dialog
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/age-check-view.fxml"));
            fxmlLoader.setController(this);
            VBox dialogRoot = fxmlLoader.load();

            // Set up the scene and stage
            Scene dialogScene = new Scene(dialogRoot);
            dialogScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/nl/inholland/view/css/main-view.css")).toExternalForm());
            dialogStage = new Stage();
            dialogStage.setScene(dialogScene);
            dialogStage.setTitle("Age Confirmation");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(parentStage);

            // Show the dialog and wait for it to close
            dialogStage.showAndWait();

            return confirmed;
        } catch (IOException e) {
            throw new RuntimeException("Error loading AgeCheck view: " + e.getMessage(), e);
        }
    }
}
