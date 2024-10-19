package nl.inholland.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.inholland.Database;
import nl.inholland.model.Selling;
import nl.inholland.model.Showing;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the history view in the application.
 * This class manages the display and functionality of the history table, which shows past ticket sales.
 * It implements the {@link Initializable} interface to set up the view when the controller is loaded.
 */
public class HistoryController implements Initializable {
    // Formatter for date and time values in dd-MM-yyyy HH:mm format
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    // Reference to the shared Database instance
    private final Database database;

    // FXML-injected components
    @FXML
    private TableView<Selling> historyTableView;
    @FXML
    private TableColumn<Selling, String> dateTimeColumn;
    @FXML
    private TableColumn<Selling, String> showingColumn;

    /**
     * Constructor for the controller.
     *
     * @param database The database instance shared across controllers.
     */
    public HistoryController(Database database) {
        this.database = database;
    }

    /**
     * Initializes the controller and sets up initial values and cell factories for the table columns.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Selling> sells = FXCollections.observableArrayList(database.getSells());
        historyTableView.setItems(sells);
        setCellValueFactory();
        setComparatorForDateTimeColumn(); // set custom comparator for dateTimeColumn
        dateTimeColumn.setSortType(TableColumn.SortType.DESCENDING); // set default sort order
        historyTableView.getSortOrder().add(dateTimeColumn); // set default sort column
        showingColumn.setSortable(false); // disable sorting for showingColumn
    }

    /**
     * Configures the cell value factories for the dateTimeColumn and showingColumn.
     * This sets how each cell in the table will display data from the {@link Selling} objects.
     */
    private void setCellValueFactory() {
        dateTimeColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getDateTime().format(formatter))
        );
        showingColumn.setCellValueFactory(
                c -> new SimpleStringProperty(getFormattedSelectedShowing(c.getValue().getShowing()))
        );
    }

    /**
     * Configures a custom comparator for the dateTimeColumn to correctly sort dates and times.
     * This method sets up the comparator for the dateTimeColumn, parsing the date strings into
     * LocalDateTime objects before comparing them. The comparison ensures that the sorting
     * respects the chronological order of the dates.
     */
    private void setComparatorForDateTimeColumn() {
        dateTimeColumn.setComparator((date1, date2) -> {
            LocalDateTime dt1 = LocalDateTime.parse(date1, formatter);
            LocalDateTime dt2 = LocalDateTime.parse(date2, formatter);
            return dt1.compareTo(dt2);
        });
    }

    /**
     * Formats the {@link Showing} object associated with each {@link Selling} record.
     * Combines the start date and title of the showing into a single formatted string.
     *
     * @param showing The showing associated with the selling record.
     * @return A formatted string containing the start date and title of the showing.
     */
    private String getFormattedSelectedShowing(Showing showing) {
        String date = formatter.format(showing.getStartDateTime());
        return date + " " + showing.getTitle();
    }
}
