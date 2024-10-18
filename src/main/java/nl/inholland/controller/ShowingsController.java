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
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowingsController implements Initializable {
    // Reference to the shared Database instance
    private final Database database;
    private final VBox root;

    private ObservableList<Showing> showings;

    @FXML
    private TableView<Showing> showingsTableView;
    @FXML
    private Button editShowingButton;
    @FXML
    private Button deleteShowingButton;
    @FXML
    private Label errorLabel;
    @FXML
    TableColumn<Showing, String> seatsLeftColumn;
    @FXML
    TableColumn<Showing, String> startColumn;
    @FXML
    TableColumn<Showing, String> endColumn;

    public ShowingsController(Database database, VBox root) {
        this.database = database;
        this.root = root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showings = FXCollections.observableArrayList(database.getAllShowings());
        showingsTableView.setItems(showings);

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
                c -> new SimpleStringProperty(c.getValue().getTicketsSold() + "/" + c.getValue().getNumberOfSeats())
        );
    }

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

        showingsTableView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onDeleteButtonClick() {
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
    }

    private boolean showConfirmationDialog(Showing selectedShowing) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(String.format("Are you sure you want to delete \"%s\"?", selectedShowing.getTitle()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            // if user chose CANCEL or closed the dialog
            return false;
        }
    }

    @FXML
    protected void onAddButtonClick() {
        openAddEditView(true);
    }

    @FXML
    protected void onEditButtonClick() {
        openAddEditView(false);
    }

    private void openAddEditView(boolean isAdd) {
        try {
            Showing selectedShowing = showingsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/add-edit-showing-view.fxml"));
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