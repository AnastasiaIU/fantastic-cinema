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
    private Label selectedLbl;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showingService = new ShowingService();
        showings = FXCollections.observableArrayList(showingService.getAllUpcomingShowings());
        sellsTableView.setItems(showings);

        addSelectionListenerToTableView();
        setCellValueFactories();
        sellsTableView.getSortOrder().add(startColumn);
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
                c -> new SimpleStringProperty(c.getValue().getTicketsSold() + "/" + c.getValue().getNumberOfSeats())
        );
    }

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

        sellsTableView.getSelectionModel().clearSelection();
    }

    private void setSelectedShowingLabel() {
        Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String date = formatter.format(selectedShowing.getStartDateTime());
        selectedLbl.setText(date + " " + selectedShowing.getTitle());
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
