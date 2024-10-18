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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SellController implements Initializable {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Reference to the shared Database instance
    private final Database database;
    private final VBox root;

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

    public SellController(Database database, VBox root) {
        this.database = database;
        this.root = root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Showing> showings = FXCollections.observableArrayList(database.getAllUpcomingShowings());
        sellsTableView.setItems(showings);

        addSelectionListenerToTableView();
        setCellValueFactories();
        sellsTableView.getSortOrder().add(startColumn);

        selectSeatsButton.setOnAction(event -> {
            showSelectSeatsView();
        });
    }

    private void setCellValueFactories() {
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

        // Clear selection in the initial state
        sellsTableView.getSelectionModel().clearSelection();
    }

    private void setSelectedShowingLabel() {
        Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();
        String date = formatter.format(selectedShowing.getStartDateTime());
        selectedLabel.setText(date + " " + selectedShowing.getTitle());
    }

    private void showSelectSeatsView() {
        try {
            Showing selectedShowing = sellsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/select-seats-view.fxml"));
            fxmlLoader.setController(new SelectSeatsController(database, selectedShowing, root));
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
