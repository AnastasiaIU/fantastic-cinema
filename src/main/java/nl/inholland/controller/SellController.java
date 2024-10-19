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
import nl.inholland.Database;
import nl.inholland.model.Showing;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the selling view in the application.
 * This class manages the display and interaction with available showings,
 * allowing users to select a showing and proceed to select seats.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class SellController implements Initializable {
    // Formatter for date and time values in dd-MM-yyyy HH:mm format
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    // Reference to the shared Database instance
    private final Database database;
    // Reference to the root VBox container of the scene
    private final VBox root;

    // FXML-injected components
    @FXML
    private Label selectedLabel;
    @FXML
    private TableView<Showing> sellsTableView;
    @FXML
    private Button selectSeatsButton;
    @FXML
    private TableColumn<Showing, String> seatsLeftColumn;
    @FXML
    private TableColumn<Showing, String> startColumn;
    @FXML
    private TableColumn<Showing, String> endColumn;

    /**
     * Constructor for the SellController.
     *
     * @param database The database instance shared across controllers.
     * @param root     The root VBox container of the scene.
     */
    public SellController(Database database, VBox root) {
        this.database = database;
        this.root = root;
    }

    /**
     * Initializes the controller and sets up the initial state of the selling view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Showing> showings = FXCollections.observableArrayList(database.getUpcomingShowings());
        sellsTableView.setItems(showings);

        addSelectionListenerToTableView();
        setCellValueFactories();
        setComparatorForDateTimeColumns();
        sellsTableView.getSortOrder().add(startColumn); // set default sort column

        selectSeatsButton.setOnAction(event -> {
            showSelectSeatsView();
        });
    }

    /**
     * Sets the cell value factories for each column in the table.
     * This method configures how data is displayed in each column.
     */
    private void setCellValueFactories() {
        seatsLeftColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getTicketsSold() + "/" + c.getValue().getNumberOfSeats())
        );
        endColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().plusSeconds(c.getValue().getDuration().toSecondOfDay()).format(formatter))
        );
        startColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().format(formatter))
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
            LocalDateTime dt1 = LocalDateTime.parse(date1, formatter);
            LocalDateTime dt2 = LocalDateTime.parse(date2, formatter);
            // Compare the parsed LocalDateTime objects
            return dt1.compareTo(dt2);
        });
    }

    /**
     * Adds a selection listener to the TableView to enable or disable the "Select Seats" button
     * based on whether an item is selected.
     */
    private void addSelectionListenerToTableView() {
        sellsTableView.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection.intValue() != -1) {
                        selectSeatsButton.setDisable(false);
                        setSelectedShowingLabel();
                    } else {
                        selectSeatsButton.setDisable(true);
                    }
                }
        );

        // Clear selection in the initial state
        sellsTableView.getSelectionModel().clearSelection();
    }

    /**
     * Sets the label to display information about the currently selected showing.
     */
    private void setSelectedShowingLabel() {
        Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();
        String date = formatter.format(selectedShowing.getStartDateTime());
        selectedLabel.setText(date + " " + selectedShowing.getTitle());
    }

    /**
     * Opens the seat selection view when the "Select Seats" button is clicked.
     * It loads the new scene and sets the controller for the select seats view.
     */
    private void showSelectSeatsView() {
        try {
            Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/select-seats-view.fxml"));
            fxmlLoader.setController(new SelectSeatsController(database, root, selectedShowing));
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
