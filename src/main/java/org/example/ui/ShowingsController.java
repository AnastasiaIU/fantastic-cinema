package org.example.ui;

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
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowingsController extends BaseController implements Initializable {
    private ShowingService showingService;

    private ObservableList<Showing> showings;

    @FXML
    private TableView<Showing> showingsTableView;
    @FXML
    private Button editShowingButton;
    @FXML
    private Button deleteShowingButton;
    @FXML
    private Label errorLbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showingService = new ShowingService();
        showings = FXCollections.observableArrayList(showingService.getAllShowings());
        showingsTableView.setItems(showings);

        showingsTableView.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection.intValue() != -1) {
                        editShowingButton.setDisable(false);
                        deleteShowingButton.setDisable(false);
                    }
                    else {
                        editShowingButton.setDisable(true);
                        deleteShowingButton.setDisable(true);
                    }
                    errorLbl.setVisible(false);
                }
        );

        showingsTableView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Showing selectedShowing = showingsTableView.getSelectionModel().getSelectedItem();

        if (selectedShowing.isTicketsSold()) {
            errorLbl.setVisible(true);
        }
        else {
            boolean userChoice = showConfirmationDialog(selectedShowing);

            if (userChoice) {
                showingService.deleteShowing(selectedShowing);
                showings.remove(selectedShowing);
            }
        }
    }

    private boolean showConfirmationDialog(Showing selectedShowing) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(String.format("Are you sure you want to delete \"%s\"?", selectedShowing.getTitle()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
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
        Showing selectedShowing = showingsTableView.getSelectionModel().getSelectedItem();

        openAddEditView(false);
    }

    private void openAddEditView(boolean isAdd) {
        try {
            Showing selectedShowing = showingsTableView.getSelectionModel().getSelectedItem();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-edit-showing-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage)showingsTableView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/css/add-edit-showing-view.css").toExternalForm());

            AddEditShowingController controller = fxmlLoader.getController();
            controller.initialize(currentUser, showingsMenu, selectedShowing, isAdd);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
