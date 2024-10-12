package org.example.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Showing;
import org.example.service.ShowingService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SellController extends BaseController implements Initializable {
    private ShowingService showingService;

    private ObservableList<Showing> showings;

    @FXML
    private TableView<Showing> sellsTableView;
    @FXML
    private Button selectSeatsButton;
    @FXML
    TableColumn<Showing, String> seatsLeftColumn;
    @FXML
    TableColumn<Showing, String> startColumn;
    @FXML
    TableColumn<Showing, String> endColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showingService = new ShowingService();
        showings = FXCollections.observableArrayList(showingService.getAllShowings());
        sellsTableView.setItems(showings);

        addSelectionListenerToTableView();
        setCellValueFactories();
    }

    private void setCellValueFactories() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        startColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().format(formatter))
        );
        endColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStartDateTime().plusSeconds(c.getValue().getDuration().toSecondOfDay()).format(formatter))
        );
        seatsLeftColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getTicketsSold() + "/" + c.getValue().getSeats())
        );
    }

    private void addSelectionListenerToTableView() {
        sellsTableView.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection.intValue() != -1) {
                        selectSeatsButton.setDisable(false);
                    } else {
                        selectSeatsButton.setDisable(true);
                    }
                }
        );

        sellsTableView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onSelectSeatsButtonClick() {
        openSelectSeatsView();
    }

    private void openSelectSeatsView() {
        try {
            Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("select-seats-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage)sellsTableView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/css/select-seats-view.css").toExternalForm());

            SelectSeatsController controller = fxmlLoader.getController();
            controller.initialize(currentUser, showingsMenu, selectedShowing);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
