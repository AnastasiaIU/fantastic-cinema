package nl.inholland.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.inholland.model.Selling;
import nl.inholland.service.SellingService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistoryController extends BaseController implements Initializable {
    private SellingService sellingService;

    private ObservableList<Selling> sellings;

    @FXML
    private TableView<Selling> historyTableView;
    @FXML
    private TableColumn<Selling, String> dateTimeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sellingService = new SellingService();
        sellings = FXCollections.observableArrayList(sellingService.getSellings());
        historyTableView.setItems(sellings);

        setCellValueFactory();
    }

    private void setCellValueFactory() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        dateTimeColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getDateTime().format(formatter))
        );
    }
}
