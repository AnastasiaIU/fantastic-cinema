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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    // Reference to the shared Database instance
    private final Database database;

    @FXML
    private TableView<Selling> historyTableView;
    @FXML
    private TableColumn<Selling, String> dateTimeColumn;
    @FXML
    private TableColumn<Selling, String> showingColumn;

    public HistoryController(Database database) {
        this.database = database;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Selling> sells = FXCollections.observableArrayList(database.getSells());
        historyTableView.setItems(sells);
        setCellValueFactory();
        historyTableView.getSortOrder().add(dateTimeColumn);
    }

    private void setCellValueFactory() {
        dateTimeColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getDateTime().format(formatter))
        );
        showingColumn.setCellValueFactory(
                c -> new SimpleStringProperty(getFormattedSelectedShowing(c.getValue().getShowing()))
        );
    }

    private String getFormattedSelectedShowing(Showing showing) {
        String date = formatter.format(showing.getStartDateTime());
        return date + " " + showing.getTitle();
    }
}
