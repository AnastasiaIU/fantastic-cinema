package org.example.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Movie;
import org.example.model.Showing;
import org.example.model.User;
import org.example.service.ShowingService;

import java.io.IOException;

public class AddEditShowingController extends BaseController {
    private ShowingService showingService;
    private Showing selectedShowing;
    private boolean isAdd;
    private ObservableList<Movie> movies;

    @FXML
    private Label titleLbl;
    @FXML
    private Button confirmButton;
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private TextField titleTextField;

    public void initialize(User currentUser, Menu clickedMenu, Showing selectedShowing, boolean isAdd) {
        super.initialize(currentUser, clickedMenu);

        showingService = new ShowingService();
        this.selectedShowing = selectedShowing;
        this.isAdd = isAdd;
        titleLbl.setText(isAdd ? "Add Showing" : "Edit Showing");
        confirmButton.setText(isAdd ? "Add showing" : "Edit showing");
        movies = FXCollections.observableArrayList(showingService.getAllMovies());
    }

    @FXML
    protected void onConfirmButtonClick() {
    }

    @FXML
    protected void onCancelButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showings-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage)titleLbl.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/css/showings-view.css").toExternalForm());

            InitializableMenu controller = fxmlLoader.getController();
            controller.initialize(currentUser, showingsMenu);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
