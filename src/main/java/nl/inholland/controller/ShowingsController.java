package nl.inholland.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import nl.inholland.Database;
import nl.inholland.model.Showing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for managing the showings view in the application.
 * This class displays the list of showings, and allows users to add, edit, or delete showings.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class ShowingsController implements Initializable {
    // Formatter for date and time values in dd-MM-yyyy HH:mm format
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Reference to the shared Database instance
    private final Database database;
    // Reference to the root VBox container of the scene
    private final VBox root;
    // ObservableList to hold the list of showings
    private ObservableList<Showing> showings;

    // FXML-injected components
    @FXML
    private TableView<Showing> showingsTableView;
    @FXML
    private Button addShowingButton;
    @FXML
    private Button editShowingButton;
    @FXML
    private Button deleteShowingButton;
    @FXML
    private Button exportShowingButton;
    @FXML
    private Label errorLabel;
    @FXML
    private TableColumn<Showing, String> seatsLeftColumn;
    @FXML
    private TableColumn<Showing, String> startColumn;
    @FXML
    private TableColumn<Showing, String> endColumn;

    /**
     * Constructor for the ShowingsController.
     *
     * @param database The database instance shared across controllers.
     * @param root     The root VBox container of the scene.
     */
    public ShowingsController(Database database, VBox root) {
        this.database = database;
        this.root = root;
    }

    /**
     * Initializes the controller and sets up the initial state of the showings view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showings = FXCollections.observableArrayList(database.getShowings());
        showingsTableView.setItems(showings);
        setCellValueFactories();
        setComparatorForDateTimeColumns();
        showingsTableView.getSortOrder().add(startColumn); // set default sort column
        addSelectionListenerToTableView();
        addListenersToButtons();
    }

    /**
     * Configures the cell value factories for the TableView columns to display the start time, end time, and seats left.
     */
    private void setCellValueFactories() {
        startColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().format(FORMATTER))
        );
        endColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().plusSeconds(c.getValue().getDuration().toSecondOfDay()).format(FORMATTER))
        );
        seatsLeftColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getTicketsSold() + "/" + c.getValue().getNumberOfSeats())
        );
    }

    /**
     * Sets custom comparators for columns containing date-time values to allow proper sorting.
     * This method assigns a comparator to the start and end columns, ensuring that they are sorted
     * based on parsed LocalDateTime values.
     */
    private void setComparatorForDateTimeColumns() {
        setComparatorForColumn(startColumn);
        setComparatorForColumn(endColumn);
    }

    /**
     * Assigns a comparator to a specified table column that compares date-time values as strings.
     * The comparator parses the string values into LocalDateTime objects using the formatter and
     * compares them, enabling accurate sorting in the table.
     *
     * @param column The TableColumn containing date-time values as strings that need to be sorted.
     */
    private void setComparatorForColumn(TableColumn<Showing, String> column) {
        column.setComparator((date1, date2) -> {
            // Parse the date strings into LocalDateTime objects using the formatter
            LocalDateTime dt1 = LocalDateTime.parse(date1, FORMATTER);
            LocalDateTime dt2 = LocalDateTime.parse(date2, FORMATTER);
            // Compare the parsed LocalDateTime objects
            return dt1.compareTo(dt2);
        });
    }

    /**
     * Adds a listener to the TableView to enable or disable the edit and delete buttons based on selection.
     */
    private void addSelectionListenerToTableView() {
        showingsTableView.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection.intValue() != -1) {
                        editShowingButton.setDisable(false);
                        deleteShowingButton.setDisable(false);
                    } else {
                        editShowingButton.setDisable(true);
                        deleteShowingButton.setDisable(true);
                    }
                    errorLabel.setVisible(false);
                }
        );

        // Clear selection in the initial state
        showingsTableView.getSelectionModel().clearSelection();
    }

    /**
     * Adds action listeners to the add, edit, and delete buttons for managing showings.
     */
    private void addListenersToButtons() {
        deleteShowingButton.setOnAction(event -> {
            Showing selectedShowing = showingsTableView.getSelectionModel().getSelectedItem();

            if (selectedShowing.isTicketsSold()) {
                errorLabel.setVisible(true);
            } else {
                boolean userChoice = showConfirmationDialog(selectedShowing);

                if (userChoice) {
                    database.deleteShowing(selectedShowing);
                    showings.remove(selectedShowing);
                }
            }
        });

        addShowingButton.setOnAction(event -> {
            openAddEditView(true);
        });

        editShowingButton.setOnAction(event -> {
            openAddEditView(false);
        });

        exportShowingButton.setOnAction(event -> exportShowingsToCSV());
    }

    /**
     * Opens a FileChooser dialog to allow the user to save the showings data as a CSV file.
     * The method configures the FileChooser to accept only files with a .csv extension.
     * If the user selects a file, it proceeds to write the showings data into the selected file.
     */
    private void exportShowingsToCSV() {
        // Configure the FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Showings as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show the Save dialog
        File file = fileChooser.showSaveDialog(exportShowingButton.getScene().getWindow());

        if (file != null) {
            writeShowingsToCSV(file);
        }
    }

    /**
     * Writes the showings data to the specified file in CSV format.
     * The method iterates through all the showings in the database and writes each showing's details
     * (start date and time, end date and time, movie title, and available seats) to the file in a structured format.
     *
     * @param file The file where the CSV data will be written.
     */
    private void writeShowingsToCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write the data for each showing
            for (Showing showing : database.getShowings()) {
                LocalDateTime startDateTime = showing.getStartDateTime();
                LocalDateTime endDateTime = startDateTime.plusSeconds(showing.getDuration().toSecondOfDay());
                String title = showing.getTitle();
                int seatsLeft = showing.getNumberOfSeats() - showing.getTicketsSold();

                // Format the data in CSV format
                writer.write(String.format("%s,%s,%s,%d\n",
                        startDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                        endDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                        title,
                        seatsLeft));
            }
        } catch (IOException e) {
            // Handle exception (e.g., show an alert dialog)
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays a confirmation dialog when attempting to delete a showing.
     *
     * @param selectedShowing The showing to be deleted.
     * @return true if the user confirms the deletion, false otherwise.
     */
    private boolean showConfirmationDialog(Showing selectedShowing) {
        // Due to lack of time a standard confirmation dialog is used which cannot be styled
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(String.format("Are you sure you want to delete \"%s\"?", selectedShowing.getTitle()));
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Opens the add/edit view for managing a showing.
     *
     * @param isAdd true if adding a new showing, false if editing an existing one.
     */
    private void openAddEditView(boolean isAdd) {
        try {
            Showing selectedShowing = isAdd ? null : showingsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/add-edit-showing-view.fxml"));
            fxmlLoader.setController(new AddEditShowingController(database, root, selectedShowing));
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
